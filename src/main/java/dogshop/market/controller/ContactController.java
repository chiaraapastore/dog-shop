package dogshop.market.controller;
import dogshop.market.entity.Contact;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @PostMapping("/send")
    public ResponseEntity<String> sendContactMessage(@RequestBody Contact contactRequest) {
        System.out.println("Messaggio ricevuto:");
        System.out.println("Nome: " + contactRequest.getName());
        System.out.println("Email: " + contactRequest.getEmail());
        System.out.println("Telefono: " + contactRequest.getPhoneNumber());
        System.out.println("Messaggio: " + contactRequest.getMessage());
        return ResponseEntity.ok("Messaggio inviato con successo!");
    }
}
