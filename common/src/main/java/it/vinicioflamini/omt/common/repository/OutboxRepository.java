package it.vinicioflamini.omt.common.repository;


import it.vinicioflamini.omt.common.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

	@Query(value = "SELECT * FROM outbox o WHERE o.processing = false ORDER BY o.date_time ASC LIMIT 1", nativeQuery=true)
	public Outbox pop(); 
}


