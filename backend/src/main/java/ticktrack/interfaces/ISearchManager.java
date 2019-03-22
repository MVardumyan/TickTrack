package ticktrack.interfaces;

import ticktrack.proto.Msg.SearchOp;

import java.util.List;

public interface ISearchManager {
    SearchOp.SearchOpResponse searchByCriteria(SearchOp.SearchOpRequest request,Integer page,Integer size);

    List<String> searchUsersByTerm(String term);
}
