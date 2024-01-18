package works.integration.microservice.repository;

import org.springframework.data.repository.CrudRepository;

import works.integration.microservice.entity.Person;
public interface PersonRepository extends CrudRepository<Person, Long>{

}
