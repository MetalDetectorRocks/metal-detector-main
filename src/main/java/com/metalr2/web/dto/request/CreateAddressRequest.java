package com.metalr2.web.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateAddressRequest {

  // todo danielw: how to validate?
  private String city;
  private String country;
  private String streetName;
  private String postalCode;
  private String type;

}
