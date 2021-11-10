--
-- SPDX-License-Identifier: Apache-2.0
--
-- Copyright 2018-2021 Agorapulse.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     https://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

// creates the book table
CREATE TABLE `book`
(
    `id`    BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `pages` INT          NOT NULL
);

// inserts the books
INSERT INTO `book` (`title`, `pages`)
VALUES ('It', 1116),
       ('The Shining', 659);

// returns the books loaded
SELECT *
FROM `book`;

