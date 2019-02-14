package classes.interfaces;

import ticktrack.proto.SearchOp;

public interface ISearchManager {
    SearchOp.SearchOpResponse searchByCriteria(SearchOp.SearchOpRequest request);
}
