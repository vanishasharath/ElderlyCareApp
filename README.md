# ElderlyCareApp
📌 Overview

The Elderly Care Android Application is a voice-enabled smart assistant designed to help elderly users manage daily activities such as medical reminders, emergency contacts, appointments, and wellness tracking. The application integrates Google Gemini Generative AI, Speech-to-Text, and Text-to-Speech, enabling hands‑free and accessible interaction.

Built with secure Firebase Authentication and Cloud Firestore, the app ensures that all personal and medical data is stored safely in the cloud, enabling usage across multiple devices with backup support.

🎯 Objectives
Provide an accessible, hands-free experience for elderly users.
Ensure secure storage and backup of sensitive personal health data.
Enable intelligent voice‑based interaction and emergency support.
Integrate real‑time AI assistance.

🧠 Key Features

🔐 User Authentication
Firebase email‑password based login & registration
Secure session management & logout functionality
One‑user‑at‑a‑time standard model for security

🏥 Medical & Personal Data Management
Appointments scheduling and viewing
Medication & general reminders with timed push notifications
Medical records and personal health notes
Daily check‑in mood and pain scale tracking
Emergency contacts + quick dial (police, ambulance, fire service)

📞 Emergency Call System
Real phone call functionality using Intent.ACTION_CALL
Android permission request & handling logic

🔔 Reminder Notifications
AlarmManager + BroadcastReceiver + NotificationManager
Works even if app is closed or in background

🎤 Voice AI Assistant
Speech‑to‑Text and Text‑to‑Speech enabled interaction
REST API via OkHttp to Google Gemini Generative AI
AI response spoken aloud and displayed on screen
STOP button to interrupt speech playback

🏗 Architecture Flow
User Speaks → STT → Gemini REST API (OkHttp) → AI Response → UI Display + TTS Output

🧑‍💻 Technologies & Tools
Android Studio (Java):	Main application development,
Firebase Authentication: Secure login & session handling,
Firebase Firestore: Cloud database & real‑time data sync,
Google Gemini Generative AI (REST API):	AI answering & conversation,
OkHttp: Client	Sending API requests,
SpeechRecognizer / RecognizerIntent:	Voice input processing,
TextToSpeech (TTS): AI voice output,
ViewBinding	: Modern UI handling,
AlarmManager & Notifications: Scheduling timed reminders,
Intent.ACTION_CALL:	Emergency calling,
Gradle Kotlin DSL (.kts):	Build configuration,

<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/4d22d4ae-b90c-4f59-8e8d-7a92a5af1470" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/1e8098a5-1476-4c07-a187-4441aebcf766" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/72fb9a29-4119-4373-b140-0f64b5cad1ef" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/6a5ede6c-3aa2-4e23-b542-43729b4b7fdc" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/606ead13-b2a2-482d-a7c4-b115bff1aec7" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/1dc365f2-14aa-4f17-930c-63d6d27c2ac5" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/62f2078e-49fb-4a8c-b844-c1e35eafdad6" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/9e1f5565-070f-4857-8a02-e0bda447dc1c" />
<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/05f7a68a-0ba1-40f4-b670-394693aa328d" />









