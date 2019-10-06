package com.metalr2.service.util;

import java.util.concurrent.Executor;

/**
 * Executes the passed runnable in the current thread instead of in a separate thread.
 * Note: Please only use the class in tests!
 */
public class CurrentThreadExecutor implements Executor {

  public void execute(Runnable r) {
    r.run();
  }

}
