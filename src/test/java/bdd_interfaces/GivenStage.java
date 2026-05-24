package bdd_interfaces;

public interface GivenStage {
    WhenStage givenUser();
    WhenStage givenUserWithAccounts(int count);
}
