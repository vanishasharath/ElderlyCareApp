# 👴👵 ElderlyCareApp

An AI-powered Android application designed to help elderly users manage daily activities through voice interaction, smart reminders, emergency support, and secure health data management.
Built with Google Gemini AI, Firebase, and Android (Java) — the app provides a hands-free, accessible experience tailored for elderly users.

--- 

## 🎯 Objectives

* Provide an accessible, hands-free experience for elderly users
* Ensure secure cloud storage and backup of sensitive personal health data
* Enable intelligent voice-based AI interaction
* Support real emergency calling and reliable background notifications

---

## 🧠 Key Features

### 🔐 User Authentication

* Firebase email‑password based login & registration
* Secure session management & logout functionality 
* One‑user‑at‑a‑time standard model for security

### 🏥 Medical & Personal Data Management

* Appointments scheduling and viewing  
* Medication & general reminders with timed push notifications
* Medical records and personal health notes
* Daily check‑in mood and pain scale tracking
* Emergency contacts + quick dial (police, ambulance, fire service)

### 📞 Emergency Call System

* Real phone call functionality using Intent.ACTION_CALL
* Android permission request & handling logic

### 🔔 Reminder Notifications

* AlarmManager + BroadcastReceiver + NotificationManager
* Notifications persist even when app is closed or in background

### 🎤 Voice AI Assistant

* Speech‑to‑Text and Text‑to‑Speech enabled interaction
* REST API via OkHttp to Google Gemini Generative AI
* AI response spoken aloud and displayed on screen 
* STOP button to interrupt speech playback

---

### 🏗 Architecture Flow

```
User Speaks
    ↓
Speech-to-Text (Android SpeechRecognizer)
    ↓
REST API Request (OkHttp → Google Gemini)
    ↓
AI Response Generated
    ↓
Display on Screen + Text-to-Speech Output
```
---

### 🧑‍💻 Technologies & Tools

Android Studio (Java):	Main application development

Firebase Authentication: Secure login & session handling

Firebase Firestore: Cloud database & real‑time data sync

Google Gemini Generative AI (REST API):	AI answering & conversation

OkHttp: Client	Sending API requests

SpeechRecognizer / RecognizerIntent:	Voice input processing

TextToSpeech (TTS): AI voice output

ViewBinding	: Modern UI handling

AlarmManager & Notifications: Scheduling timed reminders

Intent.ACTION_CALL:	Emergency calling

Gradle Kotlin DSL (.kts):	Build configuration

---

⚙️ How to Run Locally
Prerequisites

Android Studio installed
A Firebase project set up (Firebase Console)
A Google Gemini API key (Google AI Studio)

Steps

Clone the repository

bashgit clone https://github.com/your-username/ElderlyCareApp.git

Open in Android Studio

File → Open → select the project folder


Connect Firebase

Download google-services.json from your Firebase project
Place it in the /app directory


Add your Gemini API key

Open the VoiceAssistant activity file
Replace the placeholder with your API key


Build and Run

Connect an Android device or start an emulator
Click Run ▶️

---

<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/4d22d4ae-b90c-4f59-8e8d-7a92a5af1470" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/1e8098a5-1476-4c07-a187-4441aebcf766" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/72fb9a29-4119-4373-b140-0f64b5cad1ef" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/6a5ede6c-3aa2-4e23-b542-43729b4b7fdc" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/606ead13-b2a2-482d-a7c4-b115bff1aec7" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/1dc365f2-14aa-4f17-930c-63d6d27c2ac5" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/62f2078e-49fb-4a8c-b844-c1e35eafdad6" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/9e1f5565-070f-4857-8a02-e0bda447dc1c" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/05f7a68a-0ba1-40f4-b670-394693aa328d" />









