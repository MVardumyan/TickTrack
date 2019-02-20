package classes.beans;

import classes.entities.Ticket;
import classes.entities.User;
import classes.entities.UserGroup;
import classes.enums.TicketPriority;
import classes.enums.TicketStatus;
import classes.interfaces.ISearchManager;
import classes.repositories.GroupRepository;
import classes.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ticktrack.proto.Msg;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            }
        }
        //priority
        if (request.getPriorityCount() > 0) {
            currentPredicate = root.get("priority").in(
                    request.getPriorityList()
                            .stream()
                            .map(priority -> TicketPriority.valueOf(priority.toString()))
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
            }
        }
        //status
        if (request.getStatusCount() > 0) {
            currentPredicate = root.get("status").in(
                    request.getStatusList()
                            .stream()
                            .map(status -> TicketStatus.valueOf(status.toString()))
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
            }
        }

        criteriaQuery.where(criteria);
        List<Ticket> result = entityManager.createQuery(criteriaQuery).getResultList();

        return composeResponseMessageFromQueryResult(result);
    }

    private SearchOp.SearchOpResponse composeResponseMessageFromQueryResult(List<Ticket> result) {
        SearchOp.SearchOpResponse.Builder responseBuilder = SearchOp.SearchOpResponse.newBuilder();

        //todo null checks
        result.stream().map(ticket -> TicketInfo.newBuilder()
                .setTicketID(ticket.getID())
                .setSummary(ticket.getSummary())
                .setDescription(ticket.getDescription())
                .setCategory(ticket.getCategory().getName())
                .setCreator(ticket.getCreator().getUsername())
                .setAssignee(ticket.getAssignee().getUsername())
                .setResolution(ticket.getResolution())
                .setOpenDate(ticket.getOpenDate().getTime())
                .setCloseDate(ticket.getCloseDate().getTime())
                .setDeadline(ticket.getDeadline().getTime())
                .setPriority(Msg.TicketPriority.valueOf(ticket.getPriority().toString()))
                .setStatus(Msg.TicketStatus.valueOf(ticket.getStatus().toString()))
                .addAllComment(
                        ticket.getCommentList().stream().map(
                                comment -> Comment.newBuilder()
                                        .setTime(comment.getTimestamp().getTime())
                                        .setText(comment.getText())
                                        .setUsername(comment.getUsername())
                                        .build()
                        ).collect(Collectors.toList())
                )
                .build()
        ).forEach(responseBuilder::addTicketInfo);

        return responseBuilder.build();
    }
}
