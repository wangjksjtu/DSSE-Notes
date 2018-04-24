from django.contrib import admin
from .models import Ciphertext

class CiphertextAdmin(admin.ModelAdmin):
	list_display = ('id', 'keys', 'content')

admin.site.register(Ciphertext, CiphertextAdmin)
