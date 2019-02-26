package ticktrack.managers;

import ticktrack.entities.Category;
import ticktrack.repositories.CategoryRepository;
import ticktrack.interfaces.ICategoryManager;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static ticktrack.proto.Msg.*;

import java.util.Optional;
import java.util.stream.Collectors;

@Service("CategoryMng")
public class CategoryManager implements ICategoryManager {
    private final CategoryRepository categoryRepository;
    private Logger logger = LoggerFactory.getLogger(CategoryManager.class);

    @Autowired
    public CategoryManager(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public CommonResponse createCategory(String categoryName) {
        String responseText;

        if(categoryName!=null) {
            if (!categoryRepository.existsByName(categoryName)) {
                Category category = new Category(categoryName);
                categoryRepository.save(category);
                responseText = "Category" + categoryName + " created";
                logger.debug(responseText);

                return CommonResponse.newBuilder()
                        .setResponseText(responseText)
                        .setResponseType(CommonResponse.ResponseType.Success)
                        .build();
            } else {
                responseText = "Category" + categoryName + " already exists";
                logger.warn(responseText);
            }
        } else {
            responseText = "Category name is null";
            logger.warn(responseText);
        }

        return CommonResponse.newBuilder()
                .setResponseText(responseText)
                .setResponseType(CommonResponse.ResponseType.Failure)
                .build();
    }

    @Transactional
    @Override
    public CommonResponse deactivateCategory(String categoryName) {
        String responseText;
        Optional<Category> result = categoryRepository.findByName(categoryName);

        if(categoryName!=null) {
            if (result.isPresent()) {
                Category category = result.get();
                category.setDeactivated(true);
                categoryRepository.save(category);

                responseText = "Category" + categoryName + " deactivated";
                logger.debug("Category {} deactivated", responseText);

                return CommonResponse.newBuilder()
                        .setResponseText(responseText)
                        .setResponseType(CommonResponse.ResponseType.Success)
                        .build();
            } else {
                responseText = "Category" + categoryName + " not found";
                logger.warn("Category {} not found", responseText);
            }
        } else {
            responseText = "Category name is null";
            logger.warn(responseText);
        }

        return CommonResponse.newBuilder()
                .setResponseText(responseText)
                .setResponseType(CommonResponse.ResponseType.Failure)
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

                return CommonResponse.newBuilder()
                        .setResponseText(responseText)
                        .setResponseType(CommonResponse.ResponseType.Success)
                        .build();
            } else {
                responseText = "Category" + request.getOldName() + " not found";
            }
        } else {
            responseText = "Request is null";
            logger.warn(responseText);
        }

        return CommonResponse.newBuilder()
                .setResponseText(responseText)
                .setResponseType(CommonResponse.ResponseType.Failure)
                .build();
    }

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

    @Transactional
    @Override
    public CategoryOp.CategoryOpGetAllResponse getAll() {
        return CategoryOp.CategoryOpGetAllResponse.newBuilder()
                .addAllCategoryName(
                        Streams.stream(categoryRepository.findAll()).map(Category::getName).collect(Collectors.toList())
                ).build();
    }
}
