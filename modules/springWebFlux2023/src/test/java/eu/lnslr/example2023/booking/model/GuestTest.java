package eu.lnslr.example2023.booking.model;

import org.junit.jupiter.api.Test;

import static eu.lunisolar.magma.func.supp.check.Checks.attest;

class GuestTest {

    @Test void toStringWorksCorrectly() {

        attest(Guest.guest(99).toString()).mustBeEqual("Guest[preferredPrice=99.0]");

    }

}