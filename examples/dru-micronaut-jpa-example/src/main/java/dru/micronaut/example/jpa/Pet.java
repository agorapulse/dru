/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dru.micronaut.example.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Pet {
    public enum PetType {
        DOG,
        CAT
    }

    @Id
    @GeneratedValue()
    private Long id;
    private String name;
    @ManyToOne
    private Owner owner;
    private PetType type = PetType.DOG;

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public PetType getType() {
		return type;
	}

	public void setType(PetType type) {
		this.type = type;
	}

	public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pet{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", owner=" + owner +
            ", type=" + type +
            '}';
    }
}
