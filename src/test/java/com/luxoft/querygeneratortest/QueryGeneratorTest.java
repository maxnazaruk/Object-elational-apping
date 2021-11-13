package com.luxoft.querygeneratortest;

import com.luxoft.querygenerator.Person;
import com.luxoft.querygenerator.QueryGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryGeneratorTest {
    @Test
    public void testInsertInto() {
        Person person = new Person();
        person.setId(1);
        person.setSalary(250);
        person.setName("Anthony");

        String result = "INSERT INTO persons (id, person_name, salary)\nVALUES (1, 'Anthony', 250.0);";

        assertEquals(result, new QueryGenerator().insert(Person.class, person));
    }

    @Test
    public void testUpdate() {
        Person person = new Person();
        person.setId(1);
        person.setSalary(250);
        person.setName("Anthony");

        String result = "UPDATE persons\nSET id = 1, person_name = 'Anthony', salary = 250.0\nWHERE id = 4;";

        assertEquals(result, new QueryGenerator().update(Person.class, person,"id = 4"));
    }

    @Test
    public void testGetById() {
        String result = "SELECT id, person_name, salary FROM persons\nWHERE id = 4;";

        assertEquals(result, new QueryGenerator().getById(Person.class, "4"));
    }

    @Test
    public void testDelete() {
        String result = "DELETE FROM persons WHERE id = 4;";

        assertEquals(result, new QueryGenerator().delete(Person.class, "id = 4"));
    }
}
