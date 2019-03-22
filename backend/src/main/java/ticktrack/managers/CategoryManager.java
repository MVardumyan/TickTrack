package ticktrack.managers;

import ticktrack.entities.Category;
import ticktrack.proto.Msg.CategoryOp.CategoryOpGetAllResponse.CategoryInfo;
import ticktrack.repositories.CategoryRepository;
import ticktrack.interfaces.ICategoryManager;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ticktrack.proto.Msg.*;
import static ticktrack.util.ResponseHandler.*;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class provides methods for managing Category entity.
 * CategoryManager is Spring component. For db interaction it uses autowired crudRepository interfaces.
 * Contains business logic for new Category creation, changing name and deactivation.
 */
@Service("CategoryMng")
public class CategoryManager implements ICategoryManager {
    private final CategoryRepository categoryRepository;
    private Logger logger = LoggerFactory.getLogger(CategoryManager.class);

    @Autowired
    public CategoryManager(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Method for new category creation.
     * @param categoryName new category name
     * @return protobuf type CommonResponse with responseType: 1) success if category created; 2) failure if category exists or given name is null
     */
    @Transactional
    @Override
    public CommonResponse createCategory(String categoryName) {
        String responseText;

        if(categoryName!=null) {
            if (categoryRepository.existsByName(categoryName)) {
                responseText = "Category" + categoryName + " already exists";
                logger.warn(responseText);
            } else {
                Category category = new Category(categoryName);
                categoryRepository.save(category);
                responseText = "Category " + categoryName + " created";
                logger.debug(responseText);

                return buildSuccessResponse(responseText);
            }
        } else {
            responseText = "Category name is null";
            logger.warn(responseText);
        }

        return buildFailureResponse(responseText);
    }

    /**
     * Method for category deactivation. Sets Category entities field isDeactivated to true
     * @param categoryName corresponding category name
     * @return protobuf type CommonResponse with responseType: 1) success if category deactivated; 2) failure if category not found / given name is null
     */
    @Transactional
    @Override
    public CommonResponse deactivateCategory(String categoryName) {
        String responseText;

        if (categoryName == null) {
            responseText = "Category name is null";
            logger.warn(responseText);
        } else {
            Category category = get(categoryName);
            if (category == null) {
                responseText = "Category" + categoryName + " not found";
                logger.warn("Category {} not found", responseText);
            } else {
                category.setDeactivated(true);
                categoryRepository.save(category);

                responseText = "Category " + categoryName + " deactivated";
                logger.debug("Category {} deactivated", responseText);

                return buildSuccessResponse(responseText);
            }
        }

        return buildFailureResponse(responseText);
    }

    /**
     * Method for category name update.
     * @param request protobuf type CategoryOpUpdateRequest contains old and new names
     * @return protobuf type CommonResponse with responseType: 1) success if name updated; 2) failure if old name does not match/category deactivated
     */
    @Transactional
    @Override
    public CommonResponse changeName(CategoryOp.CategoryOpUpdateRequest request) {
        String responseText;

        if (request == null) {
            responseText = "Request is null";
            logger.warn(responseText);
        } else {
            Category category = get(request.getOldName());

            if (category == null) {
                responseText = "Category " + request.getOldName() + " not found";
            } else {
                if(category.isDeactivated()) {
                    responseText = "Unable to update deactivated category" + category.getName();
                    logger.warn(responseText);

                    return buildFailureResponse(responseText);
                }
                category.setName(request.getNewName());
                categoryRepository.save(category);

                responseText = "Category name " + request.getOldName() + " updated to " + request.getNewName();
                logger.debug(responseText);

                return buildSuccessResponse(responseText);
            }
        }

        return buildFailureResponse(responseText);
    }

    /**
     * Method used inside CategoryManager for searching category by name
     * @param name category name
     * @return 1) Category entity; 2) null if not found
     */
    @Transactional
    public Category get(String name) {
        Optional<Category> result = categoryRepository.findByName(name);

        if (result.isPresent()) {
            logger.debug("Query for {} category received", name);
            return result.get();
        } else {
            logger.debug("Category {} not found", name);
            return null;
        }
    }

    /**
     * Method for getting all categories from db.
     * @return protobuf type CategoryOpGetAllResponse containing list of CategoryInfo objects. Each object contains category name and status
     */
    @Transactional
    @Override
    public CategoryOp.CategoryOpGetAllResponse getAll() {
        return CategoryOp.CategoryOpGetAllResponse.newBuilder()
                .addAllCategoryInfo(
                        Streams.stream(categoryRepository.findAll()).map(category -> CategoryInfo.newBuilder()
                                .setCategoryName(category.getName())
                                .setIsDeactivated(category.isDeactivated())
                                .build())
                        .collect(Collectors.toList())
                ).build();
    }
}
