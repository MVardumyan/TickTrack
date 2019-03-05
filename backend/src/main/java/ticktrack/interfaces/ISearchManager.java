package ticktrack.interfaces;

import ticktrack.proto.Msg.SearchOp;

import java.util.List;

public interface ISearchManager {
    SearchOp.SearchOpResponse searchByCriteria(SearchOp.SearchOpRequest request);

    List<String> searchUsersByTerm(String term);
}
