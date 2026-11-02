package tools.jackson.databind.objectid;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import tools.jackson.databind.exc.InvalidNullException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.*;

class RecursiveObjectIdSettings6359Test extends DatabindTestUtil
{
    @JsonPropertyOrder({"id", "next", "value"})
    static class Node {
        public int id;

        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
        public Node next;

        @JsonSetter(nulls = Nulls.FAIL)
        public String value;
    }

    @Test
    void failOnNullAtRoot() {
        assertThrows(InvalidNullException.class, () -> newJsonMapper().readValue("""
                {"id":1,"value":null}
                """, Node.class));
    }

    @Test
    void failOnNullInNestedValue() {
        assertThrows(InvalidNullException.class, () -> newJsonMapper().readValue("""
                {"id":1,"next":{"id":2,"value":null}}
                """, Node.class));
    }

    @Test
    void failOnNullInDeepNestedValue() {
        assertThrows(InvalidNullException.class, () -> newJsonMapper().readValue("""
                {"id":1,"next":{"id":2,"next":{"id":3,"value":null}}}
                """, Node.class));
    }

    @Test
    void retainNestedObjectIdentity() {
        Node root = newJsonMapper().readValue("""
                {"id":1,"next":{"id":2,"next":2,"value":"ok"}}
                """, Node.class);
        assertSame(root.next, root.next.next);
        assertEquals("ok", root.next.value);
    }
}
