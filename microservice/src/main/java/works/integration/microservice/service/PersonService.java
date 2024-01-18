package works.integration.microservice.service;

import works.integration.microservice.entity.Person;

public interface PersonService {

    public Person getPerson(Long id);

    public Person savePerson(Person person);

    public Person updatePerson(Long id, Person person);

    public void deletePerson(Long id);

}
