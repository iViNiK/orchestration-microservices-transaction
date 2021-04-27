package it.vinicioflamini.omt.common.repository;


import it.vinicioflamini.omt.common.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

	@Query(value = "SELECT o FROM Outbox o WHERE o.processing = false ORDER BY o.dateTime ASC LIMIT 1")
	public Outbox pop(); 
}


