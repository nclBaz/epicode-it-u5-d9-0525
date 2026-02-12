package riccardogulin.u5d9.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import riccardogulin.u5d9.entities.User;
import riccardogulin.u5d9.exceptions.ValidationException;
import riccardogulin.u5d9.payloads.UserDTO;
import riccardogulin.u5d9.payloads.UserPayload;
import riccardogulin.u5d9.services.UsersService;

import java.util.List;
import java.util.UUID;

/*

1. POST http://localhost:3001/users
2. GET http://localhost:3001/users
3. GET http://localhost:3001/users/{userId}
4. PUT http://localhost:3001/users/{userId}
5. DELETE http://localhost:3001/users/{userId}

*/

@RestController
@RequestMapping("/users")
public class UsersController {
	private final UsersService usersService;

	@Autowired
	public UsersController(UsersService usersService) {
		this.usersService = usersService;
	}

	// 1. POST http://localhost:3001/users (+ Payload)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public User createUser(@RequestBody @Validated UserDTO payload, BindingResult validationResult) {
		// @Validated serve per attivare la validazione, se non lo usiamo è come non farla

		if (validationResult.hasErrors()) {

//			String errors = validationResult.getFieldErrors().stream()
//					.map(fieldError -> fieldError.getDefaultMessage())
//					.collect(Collectors.joining(". "));
//
//			throw new ValidationException(errors);
			List<String> errorsList = validationResult.getFieldErrors()
					.stream()
					.map(fieldError -> fieldError.getDefaultMessage())
					.toList();

			throw new ValidationException(errorsList);
		} else {
			return this.usersService.save(payload);
		}

	}

	// 2. GET http://localhost:3001/users
	@GetMapping
	public Page<User> findAll(@RequestParam(defaultValue = "0") int page,
	                          @RequestParam(defaultValue = "10") int size,
	                          @RequestParam(defaultValue = "surname") String orderBy,
	                          @RequestParam(defaultValue = "asc") String sortCriteria) {

		return this.usersService.findAll(page, size, orderBy, sortCriteria);
	}

	// 3. GET http://localhost:3001/users/{userId}
	@GetMapping("/{userId}")
	public User findById(@PathVariable UUID userId) {
		return this.usersService.findById(userId);
	}

	// 4. PUT http://localhost:3001/users/{userId}
	@PutMapping("/{userId}")
	public User findByIdAndUpdate(@PathVariable UUID userId, @RequestBody UserPayload payload) {
		return this.usersService.findByIdAndUpdate(userId, payload);
	}

	// 5. DELETE http://localhost:3001/users/{userId}
	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void findByIdAndDelete(@PathVariable UUID userId) {
		this.usersService.findByIdAndDelete(userId);
	}
}
