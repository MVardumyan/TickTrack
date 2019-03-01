package ticktrack.managers;

import ticktrack.entities.Ticket;
import ticktrack.entities.User;
import ticktrack.entities.UserGroup;
import ticktrack.enums.TicketPriority;
import ticktrack.enums.TicketStatus;
import ticktrack.interfaces.ISearchManager;
import ticktrack.repositories.GroupRepository;
import ticktrack.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticktrack.proto.Msg;

import javax.persistence.EntityManager;
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
    private final Logger logger = LoggerFactory.getLogger(SearchManager.class);

    @Autowired
    public SearchManager(EntityManager entityManager, UserRepository userRepository, GroupRepository groupRepository) {
        this.entityManager = entityManager;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public SearchOp.SearchOpResponse searchByCriteria(SearchOp.SearchOpRequest request) {
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
            criteria = builder.and(criteria, currentPredicate);
            currentPredicate = builder.like(root.get("description"), "%" + request.getSummaryOrDescription() + "%");
            criteria = builder.and(criteria, currentPredicate);
        }
        //creator
        if (request.hasCreator()) {
            Optional<User> creator = userRepository.findByUsername(request.getCreator());

            if (creator.isPresent()) {
                currentPredicate = builder.equal(root.get("creator"), request.getCreator());
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
            currentPredicate = root.get("category").in(request.getCategoryList());
            criteria = builder.and(criteria, currentPredicate);
        }
        //assignee
        if (request.hasAssignee()) {
            Optional<User> assignee = userRepository.findByUsername(request.getAssignee());

            if (assignee.isPresent()) {
                currentPredicate = builder.equal(root.get("assignee"), request.getAssignee());
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
                currentPredicate = builder.equal(root.get("group"), group);
                criteria = builder.and(criteria, currentPredicate);
            } else {
                logger.warn("Group {} is not found", request.getGroup());
                return getEmptyResult();
            }
        }
        //open date
        if(request.hasOpenDateStart()) {
            if(request.hasOpenDateEnd()) {
                currentPredicate = builder.between(root.get("openDate"),
                        new Timestamp(request.getOpenDateStart()),
                        new Timestamp(request.getOpenDateEnd()));
            } else {
                currentPredicate = builder.equal(root.get("openDate"),
                        new Timestamp(request.getOpenDateStart()));
            }
            criteria = builder.and(criteria, currentPredicate);
        }
        //close date
        if(request.hasCloseDateStart()) {
            if(request.hasCloseDateEnd()) {
                currentPredicate = builder.between(root.get("closeDate"),
                        new Timestamp(request.getCloseDateStart()),
                        new Timestamp(request.getCloseDateEnd()));
            } else {
                currentPredicate = builder.equal(root.get("closeDate"),
                        new Timestamp(request.getCloseDateStart()));
            }
            criteria = builder.and(criteria, currentPredicate);
        }
        //deadline
        if(request.hasDeadlineStart()) {
            if(request.hasDeadlineEnd()) {
                currentPredicate = builder.between(root.get("deadline"),
                        new Timestamp(request.getDeadlineStart()),
                        new Timestamp(request.getDeadlineEnd()));
            } else {
                currentPredicate = builder.equal(root.get("deadline"),
                        new Timestamp(request.getDeadlineStart()));
            }
            criteria = builder.and(criteria, currentPredicate);
        }

        criteriaQuery.where(criteria);
        List<Ticket> result = entityManager.createQuery(criteriaQuery).getResultList();
        return buildTicketResponseFromQueryResult(result);
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


    private SearchOp.SearchOpResponse getEmptyResult() {
        return SearchOp.SearchOpResponse.newBuilder()
                .build();
    }
}