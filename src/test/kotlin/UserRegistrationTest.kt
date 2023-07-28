import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserRegistrationTest {

    @Nested
    inner class `when registering new user`() {

        @Test
        fun `and user does not exist, expect 200 and verification link`() {
            // Test the path if user does not exist
        }

        @Test
        fun `and user already exists, expect 400`() {
            // Test the path if user exists, should not save anything into the DB
        }
    }
}
