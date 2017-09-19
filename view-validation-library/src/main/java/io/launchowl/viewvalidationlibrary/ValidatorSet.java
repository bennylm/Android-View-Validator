package io.launchowl.viewvalidationlibrary;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ValidatorSet implements Validation {
    private Set<Validator> validators;

    public ValidatorSet() {
        this.validators = new HashSet<>();
    }

    public ValidatorSet(Validator ...validators) {
        this.validators = new HashSet<>(Arrays.asList(validators));
    }

    public boolean add(Validator validator) {
        return this.validators.add(validator);
    }

    public boolean add(Validator ...validators) {
        return this.validators.addAll(Arrays.asList(validators));
    }

    public boolean remove(Validator validator) {
        return this.validators.remove(validator);
    }

    Set<Validator> getValidators() {
        return Collections.unmodifiableSet(this.validators);
    }

    @Override
    public void validate() {
        for (Validator validator : validators) {
            validator.validate();
        }
    }
}
