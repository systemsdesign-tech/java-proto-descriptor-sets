package tech.systemsdesign.proto.descriptors;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DescriptorProtos.MethodDescriptorProto;
import com.google.protobuf.DescriptorProtos.ServiceDescriptorProto;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileDescriptorSetReader {

  public static void main(String[] args) {
    assertArgumentsPresent(args);
    List<File> descriptorFiles = getDescriptorFilesList(args[0], args[1]);
    for (File file : descriptorFiles) {
      processProtobufDescriptorSet(file);
    }
  }

  private static void processProtobufDescriptorSet(File descriptorFile) {
    log(">>> Reading descriptor set from file [" + descriptorFile + "]");
    try (FileInputStream fis = new FileInputStream(descriptorFile)) {
      FileDescriptorSet fileDescriptorSet = FileDescriptorSet.parseFrom(fis);
      for (FileDescriptorProto fileDescriptorProto : fileDescriptorSet.getFileList()) {
        log("********************");
        log("FILE NAME: " + fileDescriptorProto.getName());
        log("PACKAGE: " + fileDescriptorProto.getPackage(), 1);
        fileDescriptorProto.getMessageTypeList()
            .forEach(messageDescriptor -> printMessages(messageDescriptor, 1));
        fileDescriptorProto.getServiceList().forEach(
            service -> printServiceDefinition(service, 1));
      }
    } catch (IOException e) {
      log("Error: IOException while reading [" + descriptorFile + "]: " + e);
    }
  }

  private static void printMessages(DescriptorProto messageDescriptor, int indent) {
    String messageName = "message " + messageDescriptor.getName() + " {";
    if (messageDescriptor.getFieldCount() > 0) {
      log(messageName, indent);
      messageDescriptor.getFieldList().forEach(fieldDescriptorProto -> {
        String field = fieldDescriptorProto.getType() + " " + fieldDescriptorProto.getName() + " = "
            + fieldDescriptorProto.getNumber() + ";";
        log(field, indent + 1);
      });
      log("}", indent);
    } else {
      log(messageName + " }", indent);
    }
  }

  private static void printServiceDefinition(ServiceDescriptorProto service, int indent) {
    log("SERVICE NAME: " + service.getName(), indent);
    service.getMethodList().forEach(method -> printServiceMethod(method, indent + 1));
  }

  private static void printServiceMethod(MethodDescriptorProto method, int indent) {
    log(method.getName() + "(" + method.getInputType() + ") returns (" + method.getOutputType()
        + ")", indent);
  }

  // ********** Helper methods for loading descriptor files *********

  private static List<File> getDescriptorFilesList(String descriptorDir,
      String descriptorFileSuffix) {
    File dir = new File(descriptorDir);
    if (!dir.exists()) {
      log("Error: descriptor dir [" + descriptorDir + "] does not exist");
    }
    String[] descriptorFileNames = dir.list((dir1, name) -> name.endsWith(descriptorFileSuffix));
    List<File> files = Arrays.stream(descriptorFileNames).map(f -> new File(descriptorDir, f))
        .collect(Collectors.toList());
    if (files.size() == 0) {
      log("Error: No files found in directory [" + descriptorDir + "] with suffix ["
          + descriptorFileSuffix + "]");
      System.exit(2);
    }
    return files;
  }

  private static void assertArgumentsPresent(String[] args) {
    if (args.length < 2) {
      log("Error: Not enough arguments given");
      printUsage();
      System.exit(1);
    }
  }

  private static void log(String msg) {
    log(msg, 0);
  }

  private static void log(String msg, int indent) {
    System.out.println(" ".repeat(indent * 2) + msg);
  }

  private static void printUsage() {
    log("Usage: App <descriptorDir> <descriptorFileSuffix>");
  }
}
