package org.example.rest.repository;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.example.clientes.model.Usuario;
import org.example.rest.UserApiRest;
import org.example.rest.responses.createUpdateDelete.UserCreate;
import org.example.rest.responses.getAll.UserGetAll;
import org.example.rest.responses.getById.UserGetById;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class UserRemoteRepositoryTest {
    @Mock
    private UserApiRest userApiRest;

    @InjectMocks
    private UserRemoteRepository userRemoteRepository;

    @Test
    void getAll() throws IOException {

        var list = List.of(
                UserGetAll.builder().id(1).name("Test 01").username("test01user").email("test01user@mail.com").build(),
                UserGetAll.builder().id(2).name("Test 02").username("test02user").email("test02user@mail.com").build()
        );

        var response = Response.success(list);
        var call = mock(Call.class);
        when(call.execute()).thenReturn(response);

        when(userApiRest.getAllSync()).thenReturn(call);

        Optional<List<Usuario>> result = userRemoteRepository.getAllSync();

        assertTrue(result.isPresent());
        List<Usuario> expectedResult = list.stream()
                .map(userGetAll -> Usuario.builder()
                        .id((long) userGetAll.getId())
                        .nombre(userGetAll.getName())
                        .userName(userGetAll.getUsername())
                        .email(userGetAll.getEmail())
                        .build())
                .collect(Collectors.toList());

        assertAll(
                () -> assertEquals(2, result.get().size()),
                () -> assertEquals("Test 01", result.get().get(0).getNombre()),
                () -> assertEquals("Test 02", result.get().get(1).getNombre()),
                () -> assertEquals("test01user", result.get().get(0).getUserName()),
                () -> assertEquals("test02user", result.get().get(1).getUserName()),
                () -> assertEquals("test01user@mail.com", result.get().get(0).getEmail()),
                () -> assertEquals("test02user@mail.com", result.get().get(1).getEmail()),
                () -> assertEquals(result.get(), expectedResult)
        );

        verify(userApiRest, times(1)).getAllSync();
        verify(call, times(1)).execute();
    }

    @Test
    void getAllEmptyList() throws IOException {
        var response = Response.success(List.of());
        var call = mock(Call.class);
        when(call.execute()).thenReturn(response);
        when(userApiRest.getAllSync()).thenReturn(call);

        Optional<List<Usuario>> result = userRemoteRepository.getAllSync();

        assertTrue(result.isPresent());
        assertTrue(result.get().isEmpty());

        verify(userApiRest, times(1)).getAllSync();
        verify(call, times(1)).execute();

    }

    @Test
    void getAllSyncServerInternalError() throws IOException {
        var call = mock(Call.class);
        when(call.execute()).thenThrow(new IOException("Server internal error"));
        when(userApiRest.getAllSync()).thenReturn(call);

        Optional<List<Usuario>> result = userRemoteRepository.getAllSync();

        assertFalse(result.isPresent());

        verify(userApiRest, times(1)).getAllSync();
        verify(call, times(1)).execute();

    }

    @Test
    void getByIdSync() throws IOException {
        // Arrange
        var id = 1;
        var user = UserGetById.builder().id(id).name("Test 01").username("test01user").email("test01user@mail.com").build();
        var response = Response.success(user);
        var call = mock(Call.class);
        when(call.execute()).thenReturn(response);
        when(userApiRest.getByIdSync(id)).thenReturn(call);

        Optional<Usuario> result = userRemoteRepository.getByIdSync(id);

        assertTrue(result.isPresent());
        Usuario expectedResult = Usuario.builder()
               .id((long) user.getId())
               .nombre(user.getName())
               .userName(user.getUsername())
               .email(user.getEmail())
               .build();

        assertEquals(expectedResult, result.get());

        verify(userApiRest, times(1)).getByIdSync(1L);
        verify(call, times(1)).execute();
    }

    @Test
    void getByIdNotFound() throws IOException {
        // Arrange
        var id = 1;
        var response = Response.error(404, ResponseBody.create(null, String.valueOf(MediaType.get("application/json"))));
        var call = mock(Call.class);
        when(call.execute()).thenReturn(response);
        when(userApiRest.getByIdSync(id)).thenReturn(call);

        Optional<Usuario> result = userRemoteRepository.getByIdSync(id);

        assertFalse(result.isPresent());

        verify(userApiRest, times(1)).getByIdSync(1L);
        verify(call, times(1)).execute();
    }

    @Test
    void getByIdSyncServerInternalError() throws IOException {
        // Arrange
        var id = 1;
        var call = mock(Call.class);
        when(call.execute()).thenThrow(new IOException("Server internal error"));
        when(userApiRest.getByIdSync(id)).thenReturn(call);

        Optional<Usuario> result = userRemoteRepository.getByIdSync(id);

        assertFalse(result.isPresent());

        verify(userApiRest, times(1)).getByIdSync(1L);
        verify(call, times(1)).execute();
    }

    @Test
    void createUserSync() throws IOException {
        // Arrange
        var user = Usuario.builder().nombre("Test 01").userName("test01user").email("test01user@mail.com").build();
        var response = Response.success(UserCreate.builder().id(1).name("Test 01").username("test01user").email("test01user@mail.com").build());
        var call = mock(Call.class);
        when(call.execute()).thenReturn(response);
        when(userApiRest.createUserSync(any(org.example.rest.responses.createUpdateDelete.Request.class))).thenReturn(call);

        Optional<Usuario> result = userRemoteRepository.createUserSync(user);

        assertTrue(result.isPresent());
        assertEquals(response.body().getId(), result.get().getId());
        assertEquals(response.body().getName(), result.get().getNombre());
        assertEquals(response.body().getUsername(), result.get().getUserName());
        assertEquals(response.body().getEmail(), result.get().getEmail());
        verify(userApiRest, times(1)).createUserSync(any(org.example.rest.responses.createUpdateDelete.Request.class));
        verify(call, times(1)).execute();

    }

    @Test
    void updateUserSync() {
    }

    @Test
    void deleteUserSync() {
    }
}