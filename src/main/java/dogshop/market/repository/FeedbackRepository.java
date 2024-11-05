package dogshop.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dogshop.market.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}

