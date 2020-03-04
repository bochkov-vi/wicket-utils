/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bochkov.wicket.data.provider;

import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * Allows the use of lists with {@link DataView}. The only requirement is that either list items
 * must be serializable or model(Object) needs to be overridden to provide the proper model
 * implementation.
 *
 * @param <T>
 * @author Igor Vaynberg ( ivaynberg )
 */
public abstract class ListDataProvider<T> implements IDataProvider<T> {

    private static final long serialVersionUID = 1L;

    /**
     * reference to the list used as dataprovider for the dataview
     */
    private final IModel<List<T>> list;

    /**
     * Constructs an empty provider. Useful for lazy loading together with {@linkplain #getData()}
     */
    public ListDataProvider() {
        this(new ListModel<>());
    }

    /**
     * @param list the list used as dataprovider for the dataview
     */
    public ListDataProvider(IModel<List<T>> list) {
        if (list == null) {
            throw new IllegalArgumentException("argument [list] cannot be null");
        }

        this.list = list;
    }

    /**
     * Subclass to lazy load the list
     *
     * @return The list
     */
    protected IModel<List<T>> getData() {
        return list;
    }

    @Override
    public Iterator<T> iterator(final long first, final long count) {
        List<T> list = getData().getObject();

        long toIndex = first + count;
        if (toIndex > list.size()) {
            toIndex = list.size();
        }
        return list.subList((int) first, (int) toIndex).listIterator();
    }

    @Override
    public long size() {
        return getData().map(Collection::size).getObject();
    }

    @Override
    public abstract IModel<T> model(T object);
}
