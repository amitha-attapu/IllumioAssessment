# Illumio Technical Assessment - Flow Log Tagging Program

## Description
This program processes a flow log file, parses each row, and maps it to a tag based on a lookup table. The lookup table is provided in a CSV file containing three columns: `dstport`, `protocol`, and `tag`. The program reads both the lookup table and the flow log file, performs the mapping, and generates two outputs:
- A count of matches for each tag.
- A count of matches for each port/protocol combination.

The output is saved to a file named `output.txt`.

## Assumptions
- **Log Format**: The program only supports the default flow log format version 2, as described in the assessment. Custom formats or other versions are not supported.
- **Log File Structure**: The flow log file is assumed to be structured as space-separated text, with no commas or headers. The `dstport` and `protocol` fields are the 6th and 7th indexes (0-based).
- **Protocol Mapping**: The program maps protocol numbers to their respective protocol names using a predefined list. If a protocol is not found in the predefined list, it defaults to "unknown".
- **Lookup File**: The lookup table is assumed to be a CSV file with headers (`dstport`, `protocol`, `tag`) and no extra spaces or invalid characters.
- **Case Insensitivity**: Matches for port and protocol in the lookup table are case-insensitive.
- **Output File**: Both the tag count and the port/protocol combination count are written to a single output file (`output.txt`).
- **Default Tag**: If a tag is not found in the lookup table for a specific `dstport`/`protocol` combination, it defaults to "Untagged".
- **Lookup Key Format**: The combination of `dstport` and `protocol` is treated as a key in the form of `dstport_protocol` in the lookup table.

## How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher.
- Ensure the paths to the log file and lookup table are correctly set in the main method.

### Running the Program
Compile the Java program:

```
javac Illumio.java
```
### Run the program, ensuring you provide the correct paths for the lookup and log files:

```
java Illumio
```
### Input Files
- **Lookup Table: A CSV file with the format dstport,protocol,tag.
- **Log File: A plain text file containing the flow logs in the format specified in the assessment.
- **Output

## The output will be written to a file called output.txt in the following format:

```
Tag Counts:
Tag,Count
sv_P2,1
sv_P1,2
email,4
Untagged,8

Port/Protocol Combination Counts:
Port,Protocol,Count
1024,tcp,1
49153,tcp,1
49155,tcp,1
443,tcp,1
23,tcp,1
49157,tcp,1
25,tcp,1
110,tcp,1
993,tcp,1
49154,tcp,1
80,tcp,1
143,tcp,2
49158,tcp,1
49156,tcp,1
```

Testing
Example Test
Use the following sample log file (logs.txt):

Copy code
2 123456789012 eni-0a1b2c3d 10.0.1.201 198.51.100.2 443 49153 6 25 20000 1620140761 1620140821 ACCEPT OK
2 123456789012 eni-4d3c2b1a 192.168.1.100 203.0.113.101 23 49154 6 15 12000 1620140761 1620140821 REJECT OK
Use the following sample lookup table (lookup.txt):

swift
Copy code
dstport,protocol,tag
443,tcp,sv_P2
23,tcp,sv_P1
Expected output (output.txt):

mathematica
Copy code
Tag Counts:
sv_P2,1
sv_P1,1
Untagged,0

Port/Protocol Combination Counts:
443,tcp,1
23,tcp,1
Edge Cases
Malformed Lines: Lines in both the lookup and log files that do not meet the expected format will be skipped with a warning message.
Missing Protocol: If a protocol number is not in the predefined mapping, it will be treated as "unknown".
Empty Lines: Empty lines in the input files will be ignored.