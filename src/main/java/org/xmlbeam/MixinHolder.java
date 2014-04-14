/**
 *  Copyright 2013 Sven Ewald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.xmlbeam;

/**
 */
public interface MixinHolder {

    /**
     * Register a new mixin for a projection interface. By letting a projection extend another
     * interface you are able to add custom behavior to projections by registering an implementation
     * (called a mixin) of this interface here. A mixin is registered per projection type. Only one
     * mixin implementation per projection and mixin type is possible. All existing and all future
     * projection instances will change. Notice that you will break projection serialization if you
     * register a non serializeable mixin.
     * 
     * @param projectionInterface
     * @param mixinImplementation
     * @return this for convenience
     */
    <S, M extends S, P extends S> XBProjector addProjectionMixin(Class<P> projectionInterface, M mixinImplementation);

    /**
     * Get the mixin implementation registered for the given projection.
     * 
     * @param projectionInterface
     * @param mixinInterface
     * @return the registered mixin implementation. null if none is present.
     */
    <S, M extends S, P extends S> M getProjectionMixin(Class<P> projectionInterface, Class<M> mixinInterface);

    /**
     * Remove the mixin implementation registered for the given projection.
     * 
     * @param projectionInterface
     * @param mixinInterface
     * @return the registered mixin implementation. null if none was present.
     */
    <S, M extends S, P extends S> M removeProjectionMixin(Class<P> projectionInterface, Class<M> mixinInterface);

}