# Release of Silverpeas

The variables used in this document:

  * VERSION\_TO\_RELEASE: the new version to release (for example 6.0)
  * NEXT\_DEV\_VERSION: the next development version (for example 6.1)
  * GPG\_PASSPHRASE: the passphrase to unlock the GPG key to use in the the artifacts signing
  * GPG\_KEY: the name of the key in your GPG key database to sign the artifact

## The different release modes
  
### <a name="maven"></a>The Maven Release Plugin

First, update in the pom.xml of the project the version of the dependencies on others Silverpeas projects for their new stable version if necessary and commit the change:

```bash
$ git commit -am "Update the SNAPSHOT dependencies to their stable version for the release NEXT_VERSION"
```

Second, prepare the release of the new version by specifying some the properties of the release:

```bash
$ mvn --batch-mode release:prepare -Dtag=VERSION_TO_RELEASE -Prelease-sign-artifacts -DreleaseVersion=VERSION_TO_RELEASE -DdevelopmentVersion=NEXT_DEV_VERSION -Darguments="-Dgpg.passphrase=GPG_PASSPHRASE -Dgpg.keyname=GPG_KEY"
```

Then, finalize the release of the new version:

```bash
$ mvn release:perform -Prelease-sign-artifacts -Darguments="-Dgpg.passphrase=GPG_PASSPHRASE -Dgpg.keyname=GPG_KEY"
```

If there is something wrong or in the case of a failure, you can rollback the release:

```bash
$ mvn release:rollback
$ git tag -d VERSION_TO_RELEASE
```

And if the tag was already pushed into the remote Git repository:

```bash
$ git push origin :VERSION_TO_RELEASE
```
    
Once released, update in the pom.xml of the project the version of the dependencies on others Silverpeas projects for their new development version if necessary and commit the changes.
(According to the dependencies (`Silverpeas-JCR-AccessControl` for example), the versions can be kept in their stable version until they haven't any change.)

```bash
$ git commit -a --amend
```
    
Finally, validate all the changes:

```bash
$ mvn clean deploy
$ git push
$ git push --tags
```
    
### <a name="by_hand"></a>By hand

#### For the Maven projects

  1. Update in the pom.xml of the project the version of the dependencies on others Silverpeas projects for their new stable version if necessary
  2. Update in the pom.xml the version of the project to VERSION\_TO\_RELEASE
  3. Don't forget to update also as above the subprojects if any.
  4. [Perform](#maven-step-4) the release of VERSION\_TO\_RELEASE
  5. Update in the pom.xml of the project the version of the dependencies on others Silverpeas project for their new development version if necessary
  6. Update in the pom.xml the version of the project to NEXT\_DEV\_VERSION.
  7. Don't forget to update also as above the subprojects if any.
  8. [Perform](#maven-step-8) the deployment of NEXT\_DEV\_VERSION
  9. [Validate](#maven-step-9) both the release and the post-release
  
<a name="maven-step-4"></a>To perform by hand the release, please execute the following command lines (step 4):
  
```bash
$ git commit -am "Prepare release VERSION_TO_RELEASE"
$ mvn clean deploy -Prelease-sign-artifacts -Dgpg.passphrase=GPG_PASSPHRASE -Dgpg.keyname=GPG_KEY
$ git tag VERSION_TO_RELEASE
```

<a name="maven-step-8"></a>To perform the post-release, please execute the following command lines (step 8):

```bash
$ git commit -am "Prepare for next development iteration"
$ mvn clean deploy
```

<a name="maven-step-9"></a>To validate the whole changes (step 9):

```bash
    $ git push
    $ git push --tags
```

#### For the Gradle projects

  1. Update in the build.gradle file of the project the version of the dependencies on others Silverpeas projects for their new stable version if necessary
  2. Update in the build.gradle the version of the project to VERSION\_TO\_RELEASE
  3. Don't forget to update also as above the subprojects if any.
  4. [Perform](#gradle-step-4) the release of VERSION\_TO\_RELEASE
  5. Update in the build.gradle of the project the version of the dependencies on others Silverpeas project for their new development version if necessary
  6. Update in the build.gradle the version of the project to NEXT\_DEV\_VERSION.
  7. Don't forget to update also as above the subprojects if any.
  8. [Perform](#gradle-step-8) the deployment of NEXT\_DEV\_VERSION
  9. [Validate](#gradle-step-9) both the release and the post-release
  
<a name="gradle-step-4"></a>To perform by hand the release, please execute the following command lines (step 4):
  
```bash
    $ git commit -am "Prepare release VERSION_TO_RELEASE"
    $ ./gradlew clean test install publish
    $ git tag VERSION_TO_RELEASE
```

<a name="gradle-step-8"></a>To perform the post-release, please execute the following command lines (step 8):
  
```bash
    $ git commit -am "Prepare for next development iteration"
    $ ./gradlew clean test install publish
```

<a name="gradle-step-9"></a>To validate the whole changes (step 9):

```bash
    $ git push
    $ git push --tags
```

## The release process

Some relationship rules:

  * `silverpeas-dependencies-bom`, `silverpeas-test-dependencies-bom` and `Silverpeas-Project` form all of them a set.
    Any change in this set implies a release of the whole set at the same version.
  * `Silverpeas-Core`, `Silverpeas-Components`, `Silverpeas-Assembly`, `Silverpeas-Setup`, and `Silverpeas-Distribution` form all them a set.
    Any change in this set implies a release of the whole set at the same version.
    Generally speaking, this whole set depends on the change of `Silverpeas-Core` and of `Silverpeas-Components`.
    If a change is required in `Silverpeas-Setup` or in `Silverpeas-Distribution`, their release will depend on the release of both `Silverpeas-Core` and of `Silverpeas-Components`.

Some definitions:

  * condition: what are the conditions for the project to be released. If those conditions aren't satisfied then the project shouldn't be released.
  * pre-release: what are the steps to follow before the release of the new version
  * mode: what is the release mode to use for releasing the project

Now the ordered process:

1. Silverpeas-JCR-AccessControl
    * *condition*: modified since the last release
    * *mode*: [with Maven Release plugin](#maven)

2. Silverpeas-Jackrabbit-JCA
    * *condition*: `Silverpeas-JCR-AccessControl` is released or a new stable version of Jackrabbit
    * *pre-release*: update the dependency on `Silverpeas-JCR-AccessControl`
    * *mode*: [by hand](#by_hand)

3. silverpeas-dependencies-bom
    * *condition*: either itself or `Silverpeas-Jackrabbit-JCA` has been modified since the last release 
    * *pre-release*: update the dependency on `Silverpeas-JCR-AccessControl` if it was previously released
    * *mode*: [with Maven Release plugin](#maven)

4. silverpeas-test-dependencies-bom
    * *condition*: either itself or `silverpeas-dependencies-bom`
    * *mode*: [with Maven Release plugin](#maven)
    
5. Silverpeas-Project
    * *condition*: `silverpeas-dependencies` and `silverpeas-test-dependencies` are released at the same version the project should be released
    * *pre-release*: update the dependencies on both `silverpeas-depencencies` and `silverpeas-test-dependencies`
    * *mode*: [with Maven Release plugin](#maven)
    
6. Silverpeas-Core
    * *pre-release*: update the dependency of the parent POM on the latest stable version of `Silverpeas-Project`
    * *mode*: [with Maven Release plugin](#maven)
    
7. Silverpeas-Components
    * *pre-release*: update the dependency of the parent POM on the latest stable version of `Silverpeas-Project`
    * *mode*: [with Maven Release plugin](#maven)
    
8. Silverpeas-Assembly
    * *condition*: `Silverpeas-Core` and `Silverpeas-Components` are released
    * *pre-release*: update the dependency of the parent POM on the latest stable version of `Silverpeas-Project` and update the dependency on `Silverpeas-Jackrabbit-JCA` if any
    * *mode*: [with Maven Release plugin](#maven)
    
9. Silverpeas-Setup
    * *condition*: Silverpeas-Assembly is released
    * *mode*: [by hand](#by_hand)
    
10. Silverpeas-Distribution
    * *condition*: `Silverpeas-Setup` is released
    * *mode*: specific, as described below
    
First, update the version of both its parent POM at its latest version; the parent POM of both `Silverpeas-Core`, `Silverpeas-Components`,
and `Silverpeas-Assembly` must be the same. Update the version of the project to the one to release and then:

```bash
    $ git commit -am "Prepare release VERSION_TO_RELEASE"
    $ ./build.sh silverpeas
    $ git tag VERSION_TO_RELEASE
```

The version to release must be the same than the released version of `Silverpeas-Core`, `Silverpeas-Components`, and
`Silverpeas-Assembly`.

Then, update the version of the project to the next development version as it is for both `Silverpeas-Core`, `Silverpeas-Component`,
and `Silverpeas-Assembly`. Then:

```bash
    $ git commit -am "Prepare for next development iteration"
    $ ./build.sh silverpeas
    $ git push
    $ git push --tags
```
