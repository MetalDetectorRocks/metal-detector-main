package com.metalr2.model;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

class BaseEntityTest implements WithAssertions {

  @Test
  @DisplayName("It is not allowed to set a new value for 'createdDateTime'")
  void reset_created_date_time_should_not_be_possible() {
    // given
    BaseEntity entity = new SimpleTestEntity();
    entity.setCreatedDateTime(new Date());

    // when
    Throwable throwable = catchThrowable(() -> entity.setCreatedDateTime(new Date()));

    // then
    assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  @DisplayName("It is not allowed to set a new value for 'createdBy'")
  void reset_created_by_should_not_be_possible() {
    // given
    BaseEntity entity = new SimpleTestEntity();
    entity.setCreatedBy("Foo");

    // when
    Throwable throwable = catchThrowable(() -> entity.setCreatedBy("Bar"));

    // then
    assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  @DisplayName("It is not allowed to set a new value for 'lastModifiedDateTime'")
  void reset_last_modified_date_time_should_not_be_possible() {
    // given
    BaseEntity entity = new SimpleTestEntity();
    entity.setLastModifiedDateTime(new Date());

    // when
    Throwable throwable = catchThrowable(() -> entity.setLastModifiedDateTime(new Date()));

    // then
    assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  @DisplayName("It is not allowed to set a new value for 'lastModifiedBy'")
  void reset_last_modified_by_should_not_be_possible() {
    // given
    BaseEntity entity = new SimpleTestEntity();
    entity.setLastModifiedBy("Foo");

    // when
    Throwable throwable = catchThrowable(() -> entity.setLastModifiedBy("Bar"));

    // then
    assertThat(throwable).isInstanceOf(UnsupportedOperationException.class);
  }

}
