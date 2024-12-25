import csv
import json

def csv_to_json(csv_file_path, json_output_path):
    data = []

    # Read the CSV file
    with open(csv_file_path, mode='r') as csv_file:
        reader = csv.reader(csv_file)
        for row in reader:
            entity_type, skull_texture = row
            data.append({
                "entityType": entity_type,
                "skullTexture": skull_texture,
                "variations": []
            })

    # Write to JSON file
    with open(json_output_path, mode='w') as json_file:
        json.dump(data, json_file, indent=4)

    print(f"JSON output saved to {json_output_path}")

# Example usage
csv_to_json("input.csv", "output.json")
