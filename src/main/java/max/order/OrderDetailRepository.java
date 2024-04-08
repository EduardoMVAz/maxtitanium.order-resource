package max.order;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderDetailRepository extends CrudRepository<OrderDetailModel, String> {

    List<OrderDetailModel> findByIdOrder(String idOrder);

    void deleteByIdOrder(String idOrder);
    
}
