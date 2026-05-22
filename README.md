# Wallet Service - JSON Application

## Profiling
![profiling](static/images/profiling.png)
Profiling dilakukan pada wallet service menggunakan IntelliJ Profiler dengan mode CPU and Allocation Profiling, di mana service dijalankan secara lokal dan diberi beban menggunakan JMeter (200 request) agar method yang relevan terpanggil cukup banyak untuk dianalisis. Justifikasi pemilihan profiling secara lokal adalah karena service yang ter-deploy (Render) tidak memungkinkan attach profiler ke JVM-nya, dan pengukuran lokal lebih akurat untuk mengisolasi performa kode aplikasi. Dua method critical yang di-profile adalah createTopUp (operasi write) dan markSuccess (operasi read, update, dan strategy pattern). Hasil flame graph menunjukkan bahwa mayoritas waktu eksekusi pada kedua method dihabiskan di operasi database. Bottleneck utama berada pada I/O database (network round-trip ke Supabase yang berlokasi di cloud). Dengan demikian, improvement yang berpotensi dilakukan bukan pada optimasi kode melainkan pada sisi infrastruktur, seperti tuning connection pool (HikariCP) dan lain sebagainya.  

## Monitoring