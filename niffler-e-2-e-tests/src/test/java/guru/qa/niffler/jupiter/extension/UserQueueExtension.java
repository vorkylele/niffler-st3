package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACEUSER = ExtensionContext.Namespace.create(UserQueueExtension.class);

    private static Map<User.UserType, Queue<UserJson>> usersQueue = new ConcurrentHashMap<>();

    static {
        Queue<UserJson> userWithFriends = new ConcurrentLinkedQueue<>();
        userWithFriends.add(bindUser("dima", "12345"));
        userWithFriends.add(bindUser("barsik", "12345"));
        usersQueue.put(User.UserType.WITH_FRIENDS, userWithFriends);

        Queue<UserJson> userInSent = new ConcurrentLinkedQueue<>();
        userInSent.add(bindUser("bee", "12345"));
        userInSent.add(bindUser("anna", "12345"));
        usersQueue.put(User.UserType.INVITATION_SENT, userInSent);

        Queue<UserJson> userInRc = new ConcurrentLinkedQueue<>();
        userInRc.add(bindUser("valentin", "12345"));
        userInRc.add(bindUser("pizzly", "12345"));
        usersQueue.put(User.UserType.INVITATION_RECEIVED, userInRc);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        List<Parameter> parameters = new ArrayList<>();
        Optional<Method> beforeEach = Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class)).findFirst();
        beforeEach.ifPresent(method -> parameters.addAll(List.of(method.getParameters())));
        parameters.addAll(List.of(context.getRequiredTestMethod().getParameters()));

        Map<User.UserType, UserJson> candidatesForTest = new ConcurrentHashMap<>();

        for (Parameter parameter : parameters) {
            if (parameter.getType().isAssignableFrom(UserJson.class) && parameter.isAnnotationPresent(User.class)) {
                User parameterAnnotation = parameter.getAnnotation(User.class);
                User.UserType userType = parameterAnnotation.userType();
                Queue<UserJson> usersQueueByType = usersQueue.get(userType);
                UserJson candidateForTest = null;
                while (candidateForTest == null) {
                    candidateForTest = usersQueueByType.poll();
                }
                candidateForTest.setUserType(userType);
                candidatesForTest.put(userType, candidateForTest);
            }
        }
        context.getStore(NAMESPACEUSER).put(getAllureId(context), candidatesForTest);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Map<User.UserType, UserJson> usersFromTest = context.getStore(NAMESPACEUSER).get(getAllureId(context), Map.class);
        for (User.UserType userTypeFromTest: usersFromTest.keySet()) {
            usersQueue.get(userTypeFromTest).add(usersFromTest.get(userTypeFromTest));
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class)
                && parameterContext.getParameter().isAnnotationPresent(User.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        User.UserType userType = parameterContext.getParameter().getAnnotation(User.class).userType();
        return (UserJson) extensionContext.getStore(NAMESPACEUSER).get(getAllureId(extensionContext), Map.class).get(userType);
    }

    private String getAllureId(ExtensionContext context) {
        AllureId allureId = context.getRequiredTestMethod().getAnnotation(AllureId.class);
        if (allureId == null) {
            throw new IllegalStateException("Annotation @AllureId must be present!");
        }
        return allureId.value();
    }

    private static UserJson bindUser(String username, String password) {
        UserJson user = new UserJson();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}