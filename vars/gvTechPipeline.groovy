#!/usr/bin/env groovy
import java.text.SimpleDateFormat

// vars/gvTechPipeline.groovy
def call(String name) {
    pipeline {
        agent any
        stages {
            stage("Step - 1") {
                steps {
                    echo "Welcome dear ${name}"
                }
            }
        }
    }
}
