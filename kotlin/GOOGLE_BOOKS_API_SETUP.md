# Google Books API Setup

The Booktrack app uses the Google Books API to search for books and automatically populate book details. This feature works in two modes:

## 🔓 Without API Key (Limited)
The app will work without an API key but with limitations:
- Very low rate limits (~1000 requests per day)
- May be throttled or blocked by Google
- Not recommended for production use

## 🔑 With API Key (Recommended)

### Step 1: Get a Google Books API Key

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the "Books API" for your project
4. Go to "Credentials" and create an API key
5. (Optional) Restrict the API key to the Books API for security

### Step 2: Configure the API Key

1. Open the `local.properties` file in the root of your project
2. Add your API key like this:
   ```
   GOOGLE_BOOKS_API_KEY=your_api_key_here
   ```
3. Rebuild the project

### Step 3: Verify Setup

1. Run the app
2. Try adding a book using the "Search Books" tab
3. If it works, you should see search results from Google Books

## 📝 Notes

- The `local.properties` file is gitignored, so your API key won't be committed to version control
- If no API key is provided, the app falls back to unauthenticated requests
- For production deployment, consider using Android's secrets-gradle-plugin or environment variables

## 🔒 Security

- Never commit API keys to version control
- Consider restricting your API key to specific APIs and apps
- Monitor your API usage in the Google Cloud Console
