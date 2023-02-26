package vsu.ru.market.controllers.responses;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String secretKey;

//    public String error(String message){
//
//    }
}
