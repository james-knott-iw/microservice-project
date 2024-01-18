package works.integration.microservice.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import works.integration.microservice.entity.Person;
import works.integration.microservice.repository.PersonRepository;

@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService {

    PersonRepository personRepository;

    @Override
    public Person getPerson(Long id) {
        return unwrap(personRepository.findById(id), id);
    }

    @Override
    public Person savePerson(Person person) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'savePerson'");
    }

    @Override
    public Person updatePerson(Long id, Person person) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePerson'");
    }

    @Override
    public void deletePerson(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletePerson'");
    }

    private Person unwrap(Optional<Person> entity, Long id) {
        if (entity.isPresent()) {
            return entity.get();
        } else {
            // TODO
            throw new EntityNotFoundException();
        }
    }

}
