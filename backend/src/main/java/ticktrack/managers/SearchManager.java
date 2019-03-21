package ticktrack.managers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ticktrack.entities.Category;
import ticktrack.entities.Ticket;
import ticktrack.entities.User;
import ticktrack.entities.UserGroup;
import ticktrack.enums.TicketPriority;
import ticktrack.enums.TicketStatus;
import ticktrack.interfaces.ISearchManager;
import ticktrack.repositories.CategoryRepository;
import ticktrack.repositories.GroupRepository;
import ticktrack.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticktrack.proto.Msg;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ticktrack.util.ResponseHandler.buildTicketResponseFromQueryResult;
import static ticktrack.proto.Msg.*;

@Service("SearchMng")
public class SearchManager implements ISearchManager {
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;
    private final Logger logger = LoggerFactory.getLogger(SearchManager.class);

    @Autowired
    public SearchManager(EntityManager entityManager, UserRepository userRepository, GroupRepository groupRepository, CategoryRepository categoryRepository) {
        this.entityManager = entityManager;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public SearchOp.SearchOpResponse searchByCriteria(SearchOp.SearchOpRequest request,Integer page,Integer size) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ticket> criteriaQuery = builder.createQuery(Ticket.class);
        Root<Ticket> root = criteriaQuery.from(Ticket.class);

        Predicate criteria = builder.conjunction();
        Predicate currentPredicate;

        //id
        if (request.getTicketIdCount() > 0) {
            currentPredicate = root.get("ID").in(request.getTicketIdList());
            criteria = builder.and(criteria, currentPredicate);
        }
        //summary or description
        if (request.hasSummaryOrDescription()) {
            currentPredicate = builder.like(root.get("summary"), "%" + request.getSummaryOrDescription() + "%");
            currentPredicate = builder.or(currentPredicate, (builder.like(root.get("description"), "%" + request.getSummaryOrDescription() + "%")));
            criteria = builder.and(criteria, currentPredicate);
        }
        //creator
        if (request.hasCreator()) {
            Optional<User> creator = userRepository.findByUsername(request.getCreator());

            if (creator.isPresent()) {
                currentPredicate = builder.equal(root.get("creator"), creator.get());
                criteria = builder.and(criteria, currentPredicate);
            } else {
                logger.warn("Creator {} not found", request.getCreator());
                return getEmptyResult();
            }
        }
        //priority
        if (request.getPriorityCount() > 0) {
            currentPredicate = root.get("priority").in(
                    request.getPriorityList()
                            .stream()
                            .map(this::mapTicketPriority)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
            criteria = builder.and(criteria, currentPredicate);
        }
        //category
        if (request.getCategoryCount() > 0) {
            currentPredicate = root.get("category").in(
                    request.getCategoryList()
                            .stream()
                            .map(this::mapCategory)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
            criteria = builder.and(criteria, currentPredicate);
        }
        //assignee
        if (request.hasAssignee()) {
            Optional<User> assignee = userRepository.findByUsername(request.getAssignee());

            if (assignee.isPresent()) {
                currentPredicate = builder.equal(root.get("assignee"), assignee.get());
                criteria = builder.and(criteria, currentPredicate);
            } else {
                logger.warn("Assignee {} not found", request.getAssignee());
                return getEmptyResult();
            }
        }
        //status
        if (request.getStatusCount() > 0) {
            currentPredicate = root.get("status").in(
                    request.getStatusList()
                            .stream()
                            .map(this::mapTicketStatus)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
            criteria = builder.and(criteria, currentPredicate);
        }
        //resolution
        if (request.hasResolution()) {
            currentPredicate = builder.like(root.get("resolution"), "%" + request.getResolution() + "%");
            criteria = builder.and(criteria, currentPredicate);
        }
        //group
        if (request.hasGroup()) {
            Optional<UserGroup> group = groupRepository.findByName(request.getGroup());
            if (group.isPresent()) {
                currentPredicate = builder.equal(root.get("group"), group.get());
                criteria = builder.and(criteria, currentPredicate);
            } else {
                logger.warn("Group {} is not found", request.getGroup());
                return getEmptyResult();
            }
        }
        //open date
        if (request.hasOpenDateStart() && request.hasOpenDateEnd()) {
            DateTime start = setStartDateTime(request.getOpenDateStart());
            DateTime end = setEndDateTime(request.getOpenDateEnd());

            currentPredicate = builder.between(root.get("openDate"),
                    new Timestamp(start.getMillis()),
                    new Timestamp(end.getMillis()));
            criteria = builder.and(criteria, currentPredicate);
        }
        //close date
        if (request.hasCloseDateStart() && request.hasCloseDateEnd()) {
            DateTime start = setStartDateTime(request.getCloseDateStart());
            DateTime end = setEndDateTime(request.getCloseDateEnd());

            currentPredicate = builder.between(root.get("closeDate"),
                    new Timestamp(start.getMillis()),
                    new Timestamp(end.getMillis()));
            criteria = builder.and(criteria, currentPredicate);
        }
        //deadline
        if (request.hasDeadlineStart() && request.hasDeadlineEnd()) {
            DateTime start = setStartDateTime(request.getDeadlineStart());
            DateTime end = setEndDateTime(request.getDeadlineEnd());

            currentPredicate = builder.between(root.get("deadline"),
                    new Timestamp(start.getMillis()),
                    new Timestamp(end.getMillis()));
            criteria = builder.and(criteria, currentPredicate);
        }

        criteriaQuery.where(criteria);

        TypedQuery<Ticket> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult((page - 1)*size);
        typedQuery.setMaxResults(size);

//        List<Ticket> result = entityManager.createQuery(criteriaQuery).getResultList();
        List<Ticket> result = typedQuery.getResultList();
        return buildTicketResponseFromQueryResult(result);
    }

    @Override
    public List<String> searchUsersByTerm(String term) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);

        criteriaQuery.where(builder.like(root.get("username"), "%" + term + "%"));
        return entityManager.createQuery(criteriaQuery).getResultList().stream().map(User::getUsername).collect(Collectors.toList());
    }

    private TicketStatus mapTicketStatus(Msg.TicketStatus status) {
        try {
            return TicketStatus.valueOf(status.toString());
        } catch (IllegalArgumentException e) {
            logger.warn("Status {} does not exist", status);
            return null;
        }
    }

    private TicketPriority mapTicketPriority(Msg.TicketPriority priority) {
        try {
            return TicketPriority.valueOf(priority.toString());
        } catch (IllegalArgumentException e) {
            logger.warn("Priority {} does not exist", priority);
            return null;
        }
    }

    private Category mapCategory(String category) {
        Optional<Category> result = categoryRepository.findByName(category);
        if (result.isPresent()) {
            return result.get();
        } else {
            logger.warn("Category " + category + " does not exist");
            return null;
        }
    }

    private SearchOp.SearchOpResponse getEmptyResult() {
        return SearchOp.SearchOpResponse.newBuilder()
                .build();
    }

    private DateTime setStartDateTime(String date) {
        return new DateTime(date)
                .withZone(DateTimeZone.forID("Asia/Yerevan"));
    }

    private DateTime setEndDateTime(String date) {
        return new DateTime(date)
                .withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59)
                .withZone(DateTimeZone.forID("Asia/Yerevan"));
    }
}
