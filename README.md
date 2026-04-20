# FocusLock 🧘‍♂️📵

FocusLock, akıllı telefon bağımlılığıyla ve dikkat dağınıklığıyla mücadele etmek için geliştirilmiş, agresif ve kesin çözümlü bir Android odaklanma (Pomodoro) uygulamasıdır. 

Standart odaklanma uygulamalarının aksine, FocusLock sadece bildirim göndermekle kalmaz; belirlediğiniz süre boyunca cihazın üzerine **dışarıdan müdahale edilemeyen bir kilit ekranı (Overlay)** çizerek cihazı fiziksel olarak kullanılamaz hale getirir.

## ✨ Özellikler

* **Tam Ekran Dijital İzolasyon:** `SYSTEM_ALERT_WINDOW` yetkisi sayesinde, home veya geri tuşlarına basılsa dahi kilit ekranı aşılamaz.
* **Zen Arayüzü ve Animasyonlar:** Kullanıcıyı strese sokmayan, Jetpack Compose ile sıfırdan çizilmiş karanlık tema, su dalgaları (ripple effect) ve süzülen ay animasyonları.
* **Kesintisiz Çalışma:** Android 14+ uyumlu Ön Plan Servisi (`FOREGROUND_SERVICE_SPECIAL_USE`) ile arka planda işletim sistemi tarafından öldürülmeden stabil çalışan geri sayım.
* **Esnek Zamanlayıcı:** 20, 30, 45, 60 ve 90 dakikalık hızlı odaklanma seansları.

## 🛠️ Kullanılan Teknolojiler

Bu proje %100 Native Android altyapısı kullanılarak modern teknolojilerle inşa edilmiştir:

* **Dil:** [Kotlin](https://kotlinlang.org/)
* **Kullanıcı Arayüzü (UI):** [Jetpack Compose](https://developer.android.com/jetpack/compose)
* **Animasyonlar:** Compose Animation API (`rememberInfiniteTransition`)
* **Sistem API'leri:** * `WindowManager` (Overlay View yönetimi)
  * `Foreground Service` (Arka plan görevleri)
  * `NotificationCompat` (Bildirim kanalları)

## 📸 Ekran Görüntüleri



| Süre Seçim Ekranı (Zen Mod) | Kilit ve Geri Sayım Ekranı |
| :---: | :---: |
| <img src="screenshot1_link" width="250"/> | <img src="screenshot2_link" width="250"/> |


   
