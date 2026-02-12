package riccardogulin.u5d9.payloads;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorsWithListDTO(String message, LocalDateTime timestamp, List<String> errors) {
}
