cmake_minimum_required(VERSION 3.0)

@

#
# Options
#
option(EXPORT_PACKAGE "Export package to the CMake local registry" OFF)

#
# Settings
#
set(INCL_DIR  ${CMAKE_CURRENT_SOURCE_DIR}/include)
set(GEN_DIR   ${CMAKE_CURRENT_BINARY_DIR}/generated)

set(CONFIG_FILE_TEMPLATE  "cmake/Config.cmake.in")
set(CONFIG_FILE_NAME      ${GEN_DIR}/${PROJECT_NAME}Config.cmake)
set(VERSION_FILE_NAME     ${GEN_DIR}/${PROJECT_NAME}ConfigVersion.cmake)
set(TARGETS_FILE_NAME     ${PROJECT_NAME}Targets)

#
# Configuragle settings
#
include(GNUInstallDirs)

set(
  NAMESPACE           "sbe"
  CACHE STRING        "Package namespace"
  FORCE
)

set(DEFAULT_BUILD_TYPE "Release")
if(NOT CMAKE_BUILD_TYPE AND NOT CMAKE_CONFIGURATION_TYPES)
  set(
    CMAKE_BUILD_TYPE  "${DEFAULT_BUILD_TYPE}"
    CACHE STRING      "Choose the type of build."
    FORCE
  )
  set_property(
    CACHE CMAKE_BUILD_TYPE
    PROPERTY STRINGS
      "Debug"
      "Release"
      "MinSizeRel"
      "RelWithDebInfo"
  )
endif()

set(
  LIBRARY_INSTALL_DIR "lib"
  CACHE PATH          "The library installation directory"
  FORCE
)

set(CONFIG_INSTALL_DIR  "${LIBRARY_INSTALL_DIR}/${PROJECT_NAME}/cmake")
set(INCLUDE_INSTALL_DIR "${LIBRARY_INSTALL_DIR}/${PROJECT_NAME}/${CMAKE_INSTALL_INCLUDEDIR}")
set(INCLUDE_EXPORT_DIR  "${CMAKE_INSTALL_INCLUDEDIR}")

#
# Target
#
add_library(${PROJECT_NAME} INTERFACE)
target_compile_definitions(${PROJECT_NAME} INTERFACE LIBRARY_HEADER_ONLY)

#
# Export
#
if(EXPORT_PACKAGE)
  export(PACKAGE ${PROJECT_NAME})
endif()

include(CMakePackageConfigHelpers)

write_basic_package_version_file(
  ${VERSION_FILE_NAME}
  VERSION       ${PROJECT_VERSION}
  COMPATIBILITY SameMajorVersion
)

configure_package_config_file(
  ${CONFIG_FILE_TEMPLATE} ${CONFIG_FILE_NAME}
  INSTALL_DESTINATION     ${LIBRARY_INSTALL_DIR}
  PATH_VARS               INCLUDE_EXPORT_DIR
)

install(
  TARGETS ${PROJECT_NAME}
  EXPORT  ${TARGETS_FILE_NAME}
  INCLUDES DESTINATION ${INCLUDE_INSTALL_DIR}
)

install(
  DIRECTORY "${INCL_DIR}/"
  DESTINATION ${INCLUDE_INSTALL_DIR}
)

install(
  FILES
    ${CONFIG_FILE_NAME}
    ${VERSION_FILE_NAME}
  DESTINATION
    ${CONFIG_INSTALL_DIR}
)

install(
  EXPORT      ${TARGETS_FILE_NAME}
  NAMESPACE   ${NAMESPACE}
  DESTINATION ${CONFIG_INSTALL_DIR}
)

#
# Package
#
include(InstallRequiredSystemLibraries)

set(CPACK_GENERATOR "STGZ;TGZ")
set(CPACK_PACKAGE_DESCRIPTION_SUMMARY ${PROJECT_DESCRIPTION})
set(CPACK_PACKAGE_EXECUTABLES "ccmake;CMake")

set(CPACK_PACKAGE_VERSION       ${PROJECT_VERSION})
set(CPACK_PACKAGE_VERSION_MAJOR ${PROJECT_VERSION_MAJOR})
set(CPACK_PACKAGE_VERSION_MINOR ${PROJECT_VERSION_MINOR})
set(CPACK_PACKAGE_VERSION_PATCH ${PROJECT_VERSION_PATCH})

set(CPACK_PACKAGE_INSTALL_DIRECTORY
  "CMake ${CMake_VERSION_MAJOR}.${CMake_VERSION_MINOR}")
set(CPACK_INSTALL_CMAKE_PROJECTS
  "${CMAKE_CURRENT_BINARY_DIR};${CMAKE_PROJECT_NAME};ALL;/")

include(CPack)
