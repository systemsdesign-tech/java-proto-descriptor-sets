# java-proto-descriptor-sets

This project is a demonstration on how to use Proto 3 descriptor sets to obtain metadata about
the messages and services defined in .proto files. [A blog post is available](https://systemsdesign.tech/)
discussing the code in this repo.

## Run

Clone the repo, then run the application:
```shell script
$ ./gradlew run
```

## Generating Protobuf File Descriptor Sets

You can enable generation of file descriptor sets by using `protoc --descriptor_set_out=FILE`.
In this gradle project, generation is enabled by setting the `task.generateDescriptorSet = true`
option. Take a closer look at [build.gradle](build.gradle):
```groovy
protobuf {
    ...
    generateProtoTasks {
        ...
        all().each { task ->
            task.generateDescriptorSet = true
            task.descriptorSetOptions.path = "$descriptorDir/${task.sourceSet.name}$descriptorFileSuffix"
        }
    }
}
```

## Why?

Occasionally when using protobuf, it can be necessary to inspect what classes have been generated by
protobuf in order to do interesting things, such as:
- Validate that endpoint methods fully implement a defined service interface
- Walk through a protobuf message to find all messages of a certain type in order to perform validation
- Generate new code based on existing definitions

