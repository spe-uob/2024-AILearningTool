const CACHE_NAME = 'my-app-cache-v1';

// URLs to be cached
const urlsToCache = [
  '/',
  '/index.html',
  '/manifest.json',
  '/img/icons/logo-192x192.png',
  '/img/icons/logo-512x512.png',
];

// Install event: cache app shell
self.addEventListener('install', event => {
  console.log('[ServiceWorker] Install');
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        console.log('[ServiceWorker] Caching app shell');
        return cache.addAll(urlsToCache);
      })
  );
});

// Activate event: clean up old caches
self.addEventListener('activate', event => {
  console.log('[ServiceWorker] Activate');
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cacheName => {
          if (cacheName !== CACHE_NAME) {
            console.log('[ServiceWorker] Removing old cache:', cacheName);
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});

// Fetch event: serve cached content when offline
self.addEventListener('fetch', event => {
  console.log('[ServiceWorker] Fetch', event.request.url);
  event.respondWith(
    caches.match(event.request)
      .then(response => {
        // Return cached response if found; otherwise, fetch from network
        return response || fetch(event.request);
      })
  );
});
