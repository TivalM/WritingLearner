# Generated by Django 3.0.3 on 2020-04-17 02:30

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Character',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False)),
                ('itself', models.CharField(max_length=1)),
            ],
        ),
        migrations.CreateModel(
            name='Sessions',
            fields=[
                ('key', models.CharField(max_length=20, primary_key=True, serialize=False)),
                ('data', models.CharField(max_length=20)),
                ('updated_time', models.DateTimeField()),
            ],
        ),
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False)),
                ('account', models.CharField(max_length=20)),
                ('name', models.CharField(max_length=20)),
                ('password', models.CharField(max_length=20)),
            ],
        ),
        migrations.CreateModel(
            name='History',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False)),
                ('learning_state', models.CharField(choices=[('LR', 'learning'), ('NL', "haven't learned"), ('FD', 'finished')], default='NL', max_length=2)),
                ('belongs_to_user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='users.User')),
                ('related_to_char', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='users.Character')),
            ],
        ),
    ]
