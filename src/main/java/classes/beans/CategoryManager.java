package classes.beans;

import classes.entities.Category;
import classes.repositories.CategoryRepository;
import classes.interfaces.ICategoryManager;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticktrack.proto.CategoryOp;
import ticktrack.proto.CommonResponse;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CategoryManager implements ICategoryManager {
    @Autowired
    private CategoryRepository categoryRepository;
    private Logger logger = LoggerFactory.getLogger(CategoryManager.class);

    @Transactional
    @Override
    public CommonResponse categoryOperation(CategoryOp.CategoryOpRequest request) {
        String responseText;
        if(request!=null) {
            CategoryOp.CategoryOpRequest.OpType operationType = request.getOpType();
            String categoryName;

            if (request.getCategoryName() != null) {
                categoryName = request.getCategoryName();

                switch (operationType) {
                    case Create:
                        if (categoryRepository.existsByName(categoryName)) {
                            responseText = "Category" + categoryName + " already exists";
                            logger.warn(responseText);
                        } else {
                            Category category = new Category(categoryName);
                            categoryRepository.save(category);
                            responseText = "Category" + categoryName + " created";
                            logger.debug(responseText);
                        }
                        break;
                    case Deactivate:
                        Optional<Category> result = categoryRepository.findByName(categoryName);

                        if (result.isPresent()) {
                            Category category = result.get();
                            category.setDeactivated(true);
                            categoryRepository.save(category);

                            responseText = "Category" + categoryName + " deactivated";
                            logger.debug("Category {} deactivated", responseText);
                        } else {
                            responseText = "Category" + categoryName + " not found";
                            logger.warn("Category {} not found", responseText);
                        }
                        break;
                    default:
                        responseText = "Invalid operation type : should be on of Create/Deactivate";
                        break;
                }
            } else {
                responseText = "Category name is null";
                logger.warn(responseText);
            }
        } else {
            responseText = "Request is null";
            logger.warn(responseText);
        }

        return CommonResponse.newBuilder()
                .setResponseText(responseText)
                .build();
    }

    @Transactional
    @Override
    public CommonResponse changeName(CategoryOp.CategoryOpUpdateRequest request) {
        String responseText;

        if(request!=null) {
            Category category = get(request.getOldName());

            if (category != null) {
                category.setName(request.getNewName());
                categoryRepository.save(category);

                responseText = "Category name" + request.getOldName() + "updated to " + request.getNewName();
                logger.debug(responseText);
            } else {
                responseText = "Category" + request.getOldName() + " not found";
            }
        } else {
            responseText = "Request is null";
            logger.warn(responseText);
        }

        return CommonResponse.newBuilder()
                .setResponseText(responseText)
                .build();
    }

    @Transactional
    @Override
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

    @Transactional
    @Override
    public CategoryOp.CategoryOpGetAllResponse getAll() {
        return CategoryOp.CategoryOpGetAllResponse.newBuilder()
                .addAllCategoryName(
                        Streams.stream(categoryRepository.findAll()).map(Category::getName).collect(Collectors.toList())
                ).build();
    }
}
