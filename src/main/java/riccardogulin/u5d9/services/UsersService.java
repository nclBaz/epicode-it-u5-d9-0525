package riccardogulin.u5d9.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import riccardogulin.u5d9.entities.User;
import riccardogulin.u5d9.exceptions.BadRequestException;
import riccardogulin.u5d9.exceptions.NotFoundException;
import riccardogulin.u5d9.payloads.UserDTO;
import riccardogulin.u5d9.payloads.UserPayload;
import riccardogulin.u5d9.repositories.UsersRepository;

import java.util.UUID;

@Service
@Slf4j
public class UsersService {
	private final UsersRepository usersRepository;

	@Autowired
	public UsersService(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	public User save(UserDTO payload) {

		// 1. Verifichiamo che l'email passata non sia già in uso
		this.usersRepository.findByEmail(payload.email()).ifPresent(user -> {
			throw new BadRequestException("L'email " + user.getEmail() + " è già in uso!");
		});

		// 2. Aggiungo dei campi "server-generated" tipo avatarURL
		User newUser = new User(payload.name(), payload.surname(), payload.email(), payload.password());
		newUser.setAvatarURL("https://ui-avatars.com/api?name=" + payload.name() + "+" + payload.surname());

		// 3. Salvo
		User savedUser = this.usersRepository.save(newUser);

		// 4. Log
		log.info("L'utente con id " + savedUser.getId() + " è stato salvato correttamente!");
		// 5. Torno l'utente salvato
		return savedUser;
	}

	public Page<User> findAll(int page, int size, String orderBy, String sortCriteria) {
		if (size > 100 || size < 0) size = 10;
		if (page < 0) page = 0;
		// ...
		Pageable pageable = PageRequest.of(page, size,
				sortCriteria.equals("desc") ? Sort.by(orderBy).descending() : Sort.by(orderBy));
		return this.usersRepository.findAll(pageable);
	}

	public User findById(UUID userId) {
		return this.usersRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException(userId));
	}

	public User findByIdAndUpdate(UUID userId, UserPayload payload) {
		// 1. Cerchiamo l'utente nel db
		User found = this.findById(userId);

		// 2. Validazione dati (esempio controllo se email è già in uso
		if (!found.getEmail().equals(payload.getEmail())) this.usersRepository.findByEmail(payload.getEmail()).ifPresent(user -> {
			throw new BadRequestException("L'email " + user.getEmail() + " è già in uso!");
		});
		// Il controllo su email già in uso lo faccio solo se l'utente sta modificando effettivamente la sua email

		// 3. Modifico l'utente trovato
		found.setName(payload.getName());
		found.setSurname(payload.getSurname());
		found.setEmail(payload.getEmail());
		found.setPassword(payload.getPassword());
		found.setAvatarURL("https://ui-avatars.com/api?name=" + payload.getName() + "+" + payload.getSurname());

		// 4. Salvo
		User modifiedUser = this.usersRepository.save(found);

		// 5. Log
		log.info("L'utente con id " + modifiedUser.getId() + " è stato modificato correttamente");

		// 6. Ritorno l'utente modificato
		return modifiedUser;
	}

	public void findByIdAndDelete(UUID userId) {
		User found = this.findById(userId);
		this.usersRepository.delete(found);
	}
}
