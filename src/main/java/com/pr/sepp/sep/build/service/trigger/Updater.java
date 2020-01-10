package com.pr.sepp.sep.build.service.trigger;

import java.util.function.Consumer;


public interface Updater<T> {

	void update(Consumer<T> t);

}
