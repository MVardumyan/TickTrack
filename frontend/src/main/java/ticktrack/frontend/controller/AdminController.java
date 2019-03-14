package ticktrack.frontend.controller;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ticktrack.frontend.util.OkHttpRequestHandler;
import ticktrack.proto.Msg;
import ticktrack.proto.Msg.CategoryOp.CategoryOpUpdateRequest;
import ticktrack.proto.Msg.UserOp.UserOpGetByRoleRequest;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static common.helpers.CustomJsonParser.*;
import static ticktrack.frontend.util.OkHttpRequestHandler.*;
import static ticktrack.proto.Msg.UserGroupOp.*;
import static ticktrack.proto.Msg.UserRole.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final OkHttpClient httpClient;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private String backendURL = "http://localhost:9201/backend/v1/";

    @Autowired
    public AdminController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    String displaySearchPage(ModelMap model) {

        return "adminMain";
    }

    @RequestMapping(value = "/userManagement", method = RequestMethod.GET)
    String displayUserManagementPage(ModelMap model) {
        UserOpGetByRoleRequest message = UserOpGetByRoleRequest.newBuilder()
                .setCriteria(UserOpGetByRoleRequest.Criteria.All)
                .build();

        Request request = OkHttpRequestHandler.buildRequestWithBody(backendURL + "users/getUsersByRole",
                protobufToJson(wrapIntoMsg(message)));

        try (Response response = httpClient.newCall(request).execute()) {
            if(response.code()==200) {
                Msg result = jsonToProtobuf(response.body().string());

                if (result != null && result.hasUserOperation() && result.getUserOperation().hasUserOpGetResponse()) {
                    List<Msg.UserOp.UserOpGetResponse.UserInfo> userInfoList = result.getUserOperation().getUserOpGetResponse().getUserInfoList();

                    model.put("regularUserInfo", userInfoList
                            .stream()
                            .filter(userInfo -> userInfo.getRole().equals(RegularUser))
                            .collect(Collectors.toList()));

                    model.put("businessUserInfo", userInfoList
                            .stream()
                            .filter(userInfo -> userInfo.getRole().equals(BusinessUser))
                            .collect(Collectors.toList()));

                    model.put("adminInfo", userInfoList
                            .stream()
                            .filter(userInfo -> userInfo.getRole().equals(Admin))
                            .collect(Collectors.toList()));

                    return "userManagement";
                }
            }
            logger.error("Received error from backend : code : {}; message : {}", response.code(), response.message());
            return "error";
        } catch (IOException e) {
            logger.error("Internal error, unable to get users list", e);
            return "error";
        }
    }

    //  CATEGORIES

    @RequestMapping(value = "/categoryManagement", method = RequestMethod.GET)
    String displayCategoryManagementPage(ModelMap model) {
        Request categoriesRequest = buildRequestWithoutBody(backendURL + "categories/getAll");

        try(Response response = httpClient.newCall(categoriesRequest).execute()) {
            if(response.code()==200) {
                Msg result = jsonToProtobuf(response.body().string());

                if(result!=null && result.hasCategoryOperation() && result.getCategoryOperation().hasCategoryOpGetAllResponse()) {
                    model.put("categories", result.getCategoryOperation().getCategoryOpGetAllResponse().getCategoryInfoList());
                    return "categoryManagement";
                }
            }
            return "error";
        } catch (IOException e) {
            logger.error("Internal error, unable to get categories list", e);
            return "error";
        }
    }

    @RequestMapping(value = "/deactivateCategory/{name}", method = RequestMethod.GET)
    String deactivateCategory(@PathVariable("name") String name) {
        Request request = buildRequestWithCategoryParam(backendURL + "categories/deactivate", name);

        return processCategoryRequest(request);
    }

    @RequestMapping(value = "/createCategory", method = RequestMethod.POST)
    String createCategory(@RequestParam(name = "newCategory") String newCategory) {
        Request request = buildRequestWithCategoryParam(backendURL + "categories/add", newCategory);

        return processCategoryRequest(request);
    }

    @RequestMapping(value = "/updateCategory", method = RequestMethod.POST)
    String updateCategory(@RequestParam(name = "oldName") String oldName, @RequestParam(name = "newName") String newName) {
        CategoryOpUpdateRequest message = CategoryOpUpdateRequest.newBuilder()
                .setOldName(oldName)
                .setNewName(newName)
                .build();

        Request request = buildRequestWithBody(backendURL + "categories/changeName",
                protobufToJson(wrapIntoMsg(message)));

        return processCategoryRequest(request);
    }

    // GROUPS

    @RequestMapping(value = "/groupManagement", method = RequestMethod.GET)
    String displayGroupManagementPage(ModelMap model) {
        Request request = buildRequestWithoutBody(backendURL + "userGroups/getAll");

        try (Response response = httpClient.newCall(request).execute()) {
            if(response.code()==200) {
                Msg result = jsonToProtobuf(response.body().string());

                if(result!=null && result.hasUserGroupOperation() && result.getUserGroupOperation().hasUserGroupOpGetAllResponse()) {
                    model.put("groups", result.getUserGroupOperation().getUserGroupOpGetAllResponse().getGroupNameList());
                    return "groupManagement";
                }
            }
            return "error";

        } catch (IOException e) {
            logger.error("Internal error, unable to get response from server", e);
            return "error";
        }
    }

    @RequestMapping(value = "/createGroup", method = RequestMethod.POST)
    String createGroup(@RequestParam(name = "newGroup") String groupName) {
        Request request = buildRequestWithoutBody(backendURL + "userGroups/add/" + groupName);

        return processGroupRequest(request);
    }

    @RequestMapping(value = "/deleteGroup/{groupName}", method = RequestMethod.GET)
    String deleteGroup(@PathVariable("groupName") String groupName) {
        Request request = buildRequestWithoutBody(backendURL + "userGroups/delete/" + groupName);

        return processGroupRequest(request);
    }

    @RequestMapping(value = "/updateGroup", method = RequestMethod.POST)
    String updateGroup(@RequestParam(name = "oldName") String oldName, @RequestParam(name = "newName") String newName) {
        UserGroupOpUpdateRequest message = UserGroupOpUpdateRequest.newBuilder()
                .setOldName(oldName)
                .setNewName(newName)
                .build();

        Request request = buildRequestWithBody(backendURL + "userGroups/changeName",
                protobufToJson(wrapIntoMsg(message)));

        return processGroupRequest(request);
    }

    @NotNull
    private String processCategoryRequest(Request request) {
        try(Response response = httpClient.newCall(request).execute()) {
            if(response.code()==200) {
                Msg result = jsonToProtobuf(response.body().string());
                if(result!=null && result.hasCommonResponse()) {
                    logger.debug(result.getCommonResponse().getResponseText());
                    return "redirect:/admin/categoryManagement";
                }
            }
            return "error";
        } catch (IOException e) {
            logger.error("Internal error, unable to get response from server", e);
            return "error";
        }
    }

    @NotNull
    private String processGroupRequest(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {
            if(response.code()==200) {
                Msg result = jsonToProtobuf(response.body().string());

                if(result!=null && result.hasCommonResponse()) {
                    logger.debug(result.getCommonResponse().getResponseText());
                    return "redirect:/admin/groupManagement";
                }
            }
            return "error";

        } catch (IOException e) {
            logger.error("Internal error, unable to get response from server", e);
            return "error";
        }
    }

    private Msg wrapIntoMsg(UserOpGetByRoleRequest request) {
        return Msg.newBuilder()
                .setUserOperation(
                        Msg.UserOp.newBuilder()
                            .setUserOpGetByRoleRequest(request)
                ).build();
    }

    private Msg wrapIntoMsg(CategoryOpUpdateRequest request) {
        return Msg.newBuilder()
                .setCategoryOperation(
                        Msg.CategoryOp.newBuilder()
                            .setCategoryOpUpdateRequest(request)
                ).build();
    }

    private Msg wrapIntoMsg(UserGroupOpUpdateRequest message) {
        return Msg.newBuilder()
                .setUserGroupOperation(
                        Msg.UserGroupOp.newBuilder()
                            .setUserGroupOpUpdateRequest(message)
                ).build();
    }

    private Request buildRequestWithCategoryParam(String targetUrl, String name) {
        HttpUrl.Builder url = HttpUrl.parse(targetUrl).newBuilder()
                .addQueryParameter("name", name);

        return new Request.Builder()
                .url(url.build())
                .build();
    }
}
