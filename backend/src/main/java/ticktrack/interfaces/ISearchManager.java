package ticktrack.interfaces;

import ticktrack.proto.Msg.SearchOp;

public interface ISearchManager {
    SearchOp.SearchOpResponse searchByCriteria(SearchOp.SearchOpRequest request);
}
