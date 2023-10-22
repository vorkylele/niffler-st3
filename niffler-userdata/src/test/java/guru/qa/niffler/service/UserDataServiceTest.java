package guru.qa.niffler.service;

import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.FriendsEntity;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.ex.NotFoundException;
import guru.qa.niffler.model.FriendJson;
import guru.qa.niffler.model.FriendState;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static guru.qa.niffler.model.FriendState.FRIEND;
import static guru.qa.niffler.model.FriendState.INVITE_SENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataServiceTest {
    private UserDataService testedObject;
    private UUID mainTestUserUuid = UUID.randomUUID();
    private String mainTestUserName = "dima";
    private UserEntity mainTestUser;
    private UUID secondTestUserUuid = UUID.randomUUID();
    private String secondTestUserName = "barsik";
    private UserEntity secondTestUser;
    private UUID thirdTestUserUuid = UUID.randomUUID();
    private String thirdTestUserName = "emma";
    private UserEntity thirdTestUser;
    private String notExistingUser = "not_existing_user";

    @BeforeEach
    void init() {
        mainTestUser = new UserEntity();
        mainTestUser.setId(mainTestUserUuid);
        mainTestUser.setUsername(mainTestUserName);
        mainTestUser.setCurrency(CurrencyValues.RUB);
        secondTestUser = new UserEntity();
        secondTestUser.setId(secondTestUserUuid);
        secondTestUser.setUsername(secondTestUserName);
        secondTestUser.setCurrency(CurrencyValues.RUB);
        thirdTestUser = new UserEntity();
        thirdTestUser.setId(thirdTestUserUuid);
        thirdTestUser.setUsername(thirdTestUserName);
        thirdTestUser.setCurrency(CurrencyValues.RUB);
    }

    @ValueSource(strings = {"photo", ""})
    @ParameterizedTest
    void userShouldBeUpdated(String photo, @Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(mainTestUser);
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(answer -> answer.getArguments()[0]);
        testedObject = new UserDataService(userRepository);
        final String photoForTest = photo.equals("") ? null : photo;
        final UserJson toBeUpdated = new UserJson();
        toBeUpdated.setUsername(mainTestUserName);
        toBeUpdated.setFirstname("Test");
        toBeUpdated.setSurname("TestSurname");
        toBeUpdated.setCurrency(CurrencyValues.USD);
        toBeUpdated.setPhoto(photoForTest);
        final UserJson result = testedObject.update(toBeUpdated);
        assertEquals(mainTestUserUuid, result.getId());
        assertEquals("Test", result.getFirstname());
        assertEquals("TestSurname", result.getSurname());
        assertEquals(CurrencyValues.USD, result.getCurrency());
        assertEquals(photoForTest, result.getPhoto());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void getRequiredUserShouldThrowNotFoundExceptionIfUserNotFound(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(notExistingUser)))
                .thenReturn(null);
        testedObject = new UserDataService(userRepository);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> testedObject.getRequiredUser(notExistingUser));
        assertEquals(
                "Can`t find user by username: " + notExistingUser,
                exception.getMessage()
        );
    }

    @Test
    void allUsersShouldReturnCorrectUsersList(@Mock UserRepository userRepository) {
        when(userRepository.findByUsernameNot(eq(mainTestUserName)))
                .thenReturn(getMockUsersMappingFromDb());
        testedObject = new UserDataService(userRepository);
        List<UserJson> users = testedObject.allUsers(mainTestUserName);
        assertEquals(2, users.size());
        final UserJson invitation = users.stream()
                .filter(u -> u.getFriendState() == INVITE_SENT)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Friend with state INVITE_SENT not found"));
        final UserJson friend = users.stream()
                .filter(u -> u.getFriendState() == FRIEND)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Friend with state FRIEND not found"));
        assertEquals(secondTestUserName, invitation.getUsername());
        assertEquals(thirdTestUserName, friend.getUsername());
    }

    static Stream<Arguments> friendsShouldReturnDifferentListsBasedOnIncludePendingParam() {
        return Stream.of(
                Arguments.of(true, List.of(INVITE_SENT, FRIEND)),
                Arguments.of(false, List.of(FRIEND))
        );
    }

    @MethodSource
    @ParameterizedTest
    void friendsShouldReturnDifferentListsBasedOnIncludePendingParam(boolean includePending,
                                                                     List<FriendState> expectedStates,
                                                                     @Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(enrichTestUser());
        testedObject = new UserDataService(userRepository);
        final List<UserJson> result = testedObject.friends(mainTestUserName, includePending);
        assertEquals(expectedStates.size(), result.size());
        assertTrue(result.stream()
                .map(UserJson::getFriendState)
                .toList()
                .containsAll(expectedStates));
    }

    private UserEntity enrichTestUser() {
        mainTestUser.addFriends(true, secondTestUser);
        secondTestUser.addInvites(mainTestUser);
        mainTestUser.addFriends(false, thirdTestUser);
        thirdTestUser.addFriends(false, mainTestUser);
        return mainTestUser;
    }

    private List<UserEntity> getMockUsersMappingFromDb() {
        mainTestUser.addFriends(true, secondTestUser);
        secondTestUser.addInvites(mainTestUser);
        mainTestUser.addFriends(false, thirdTestUser);
        thirdTestUser.addFriends(false, mainTestUser);
        return List.of(secondTestUser, thirdTestUser);
    }

    @DisplayName("Тестирование метода 'getCurrentUser' для существующего пользователя")
    @Test
    void getCurrentUserReturnsUserJsonWhenUserExists(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(mainTestUser);

        testedObject = new UserDataService(userRepository);
        UserJson result = testedObject.getCurrentUser(mainTestUserName);

        assertEquals(mainTestUserUuid, result.getId());
        assertEquals(mainTestUserName, result.getUsername());
        assertEquals(mainTestUser.getCurrency(), result.getCurrency());
    }

    @DisplayName("Тестирование метода 'getCurrentUser' для несуществующего пользователя")
    @Test
    void getCurrentUserShouldThrowsNotFoundExceptionWhenUserDoesNotExist(@Mock UserRepository userRepository) {
        when(userRepository.findByUsername(eq(notExistingUser)))
                .thenReturn(null);

        testedObject = new UserDataService(userRepository);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            testedObject.getCurrentUser(notExistingUser);
        });

        String expectedMessage = "Can`t find user by username: " + notExistingUser;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @DisplayName("Тестирование метода 'addFriend' - добавление друга")
    @Test
    void addFriendSavesUserAndReturnsInviteSentWhenBothUsersExist(@Mock UserRepository userRepository) {
        FriendJson testUserEntity = new FriendJson();
        testUserEntity.setUsername("friendUser");
        UserEntity friendUserEntity = new UserEntity();
        friendUserEntity.setUsername(testUserEntity.getUsername());

        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(mainTestUser);
        when(userRepository.findByUsername(eq(testUserEntity.getUsername())))
                .thenReturn(friendUserEntity);

        testedObject = new UserDataService(userRepository);

        UserJson result = testedObject.addFriend(mainTestUserName, testUserEntity);

        assertEquals(FriendState.INVITE_SENT, result.getFriendState());
        verify(userRepository, times(1)).save(mainTestUser);
    }

    @DisplayName("Тестирование метода 'acceptInvitation' для существующего пользователя")
    @Test
    void acceptInvitationReturnsFriendsListWhenInviteExists(@Mock UserRepository userRepository) {
        FriendJson testUserEntity = new FriendJson();
        testUserEntity.setUsername("friendUser");
        UserEntity friendUserEntity = new UserEntity();
        friendUserEntity.setUsername(testUserEntity.getUsername());

        FriendsEntity invitationEntity = new FriendsEntity();
        invitationEntity.setUser(friendUserEntity);
        mainTestUser.setInvites(Collections.singletonList(invitationEntity));


        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(mainTestUser);
        when(userRepository.findByUsername(eq(testUserEntity.getUsername())))
                .thenReturn(friendUserEntity);

        testedObject = new UserDataService(userRepository);

        List<UserJson> result = testedObject.acceptInvitation(mainTestUserName, testUserEntity);

        assertFalse(result.isEmpty());
    }

    @DisplayName("Тестирование метода 'acceptInvitation' когда юзер не найден")
    @Test
    void acceptInvitationThrowsNotFoundExceptionWhenCurrentUserDoesNotExist(@Mock UserRepository userRepository) {
        FriendJson testUserEntity = new FriendJson();
        testUserEntity.setUsername("friendUser");

        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(null);

        testedObject = new UserDataService(userRepository);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            testedObject.acceptInvitation(mainTestUserName, testUserEntity);
        });

        String expectedMessage = "Can`t find user by username: " + mainTestUserName;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @DisplayName("Тестирование метода 'acceptInvitation' когда не найдены приглашения")
    @Test
    void acceptInvitationThrowsExceptionWhenNoInviteExists(@Mock UserRepository userRepository) {
        FriendJson testUserEntity = new FriendJson();
        testUserEntity.setUsername("friendUser");
        UserEntity friendUserEntity = new UserEntity();
        friendUserEntity.setUsername(testUserEntity.getUsername());

        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(mainTestUser);
        when(userRepository.findByUsername(eq(testUserEntity.getUsername())))
                .thenReturn(friendUserEntity);

        testedObject = new UserDataService(userRepository);

        assertThrows(Exception.class, () -> {
            testedObject.acceptInvitation(mainTestUserName, testUserEntity);
        });
    }

    @DisplayName("Тестирование метода 'removeFriend'")
    @Test
    void removeFriendShouldRemoveFriendAndReturnUpdatedFriendsList(@Mock UserRepository userRepository) {
        FriendJson testUserEntity = new FriendJson();
        testUserEntity.setUsername("friendUser");

        UserEntity friendEntity = new UserEntity();
        friendEntity.setUsername(testUserEntity.getUsername());

        when(userRepository.findByUsername(eq(mainTestUserName)))
                .thenReturn(mainTestUser);
        when(userRepository.findByUsername(eq(testUserEntity.getUsername())))
                .thenReturn(friendEntity);

        testedObject = new UserDataService(userRepository);

        List<UserJson> friends = testedObject.removeFriend(mainTestUserName, testUserEntity.getUsername());

        assertFalse(mainTestUser.getFriends().contains(friendEntity));
    }
}
