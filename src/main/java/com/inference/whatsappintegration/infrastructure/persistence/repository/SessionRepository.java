package com.inference.whatsappintegration.infrastructure.persistence.repository;

import com.inference.whatsappintegration.infrastructure.persistence.entity.Sessions;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository  extends CrudRepository<Sessions, String> {

}
