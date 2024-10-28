from requests import post, patch, get
from faker import Faker
import random
import time

fake = Faker()

time.sleep(15) # Wait for the backend service

# Load the admin token to register the doctors
def load_admin_token():
    response = post("http://host.docker.internal:8000/api/v1/login", json={"email": "admin", "password":"admin"})
    return response.json()['accessToken']

# Register the doctors
def register_doctors():
    genders = ["MALE", "FEMALE"]
    admin_token = load_admin_token()

    for _ in range(100):
        email = fake.email()
        password = fake.password()
        doctor = {
            "firstName": fake.first_name(),
            "lastName": fake.last_name(),
            "email": email,
            "password": password,
            "gender": random.choice(genders),
            "licenseNumber": fake.bothify(text="????####"),
            "phoneNumber": 3240961234,
            "clinicAddress": fake.address().replace("\n", ", "),
            "specializations": [],
        }
        try:
            post("http://host.docker.internal:8000/api/v1/register/doctor", json=doctor, headers={"Authorization": "Bearer " + admin_token})
            access_token = post("http://host.docker.internal:8000/api/v1/login", json={"email": email, "password": password}).json()['accessToken']
            specializations = get_specializations(access_token)
            patchData = {
                "firstName": fake.first_name(),
                "lastName": fake.last_name(),
                "clinicAddress": fake.address().replace("\n", ", "),
                "phoneNumber": 3240961234,
                "licenseNumber": fake.bothify(text="????####"),
                "experienceYears": random.randint(1, 10),
                "specializations": random.sample(specializations, random.choice([1, 2]))
            }
            patch("http://host.docker.internal:8000/api/v1/profile/doctor", json=patchData, headers={"Authorization": "Bearer " + access_token})
            print("Successfully added a doctor!")
        except:
            print("Failed to add a doctor!")
            continue

# Get the specializations
def get_specializations(accessToken):
    return get("http://host.docker.internal:8000/api/v1/specializations", headers={"Authorization": "Bearer " + accessToken}).json()

print(register_doctors())

# Register the patients
def patients_register():
    genders = ['MALE', 'FEMALE']
    blood_types = ['APlus', 'AMinus', 'BPlus', 'BMinus', 'OPlus', 'OMinus', 'ABPlus', 'ABMinus']
    allergies_list = ['Peanuts', 'Shellfish', 'Pollen', 'Latex', 'Penicillin', 'None']


    for _ in range(100):
        password = fake.password()
        email = fake.email()
        patient = {
            "firstName": fake.first_name(),
            "lastName": fake.last_name(),
            "email": email,
            "dateOfBirth": fake.date_of_birth(minimum_age=18, maximum_age=80).strftime('%Y-%m-%d'),
            "gender": random.choice(genders),
            "password": password
        }
        try:
            post("http://host.docker.internal:8000/api/v1/register/patient", json=patient)
            access_token = post("http://host.docker.internal:8000/api/v1/login", json={"email": email, "password": password}).json()['accessToken']
            patchData = {
                "address": fake.address().replace("\n", ", "),
                "bloodType": random.choice(blood_types),
                "phoneNumber": fake.phone_number(),
                "allergies": random.choice(allergies_list),
            }
            patch("http://host.docker.internal:8000/api/v1/profile/patient", json=patchData, headers={"Authorization": f"Bearer {access_token}"})
            print("Successfully added a patient!")
        except:
            print("Failed to add a patient!")
            continue

print(patients_register())