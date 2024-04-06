package max.order;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Builder
@Getter @Setter @Accessors(fluent = true, chain = true)
@AllArgsConstructor @NoArgsConstructor
public class Order implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String id;
    private String clientId;
    private String address;
    private Map<Integer, Integer> products;
    private Integer orderValue;
}